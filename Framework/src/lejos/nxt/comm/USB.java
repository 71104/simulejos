package lejos.nxt.comm;
import lejos.nxt.NXTEvent;

/**
 * Low-level USB access.
 * 
 * @author Lawrie Griffiths, extended to support streams by Andy Shaw
 *
 */
public class USB extends NXTCommDevice {
    public static final int RESET = 0x40000000;
    static final int BUFSZ = 256;
    static final int HW_BUFSZ = 64;
    // USB events
	public static final byte USB_READABLE = 1;
	public static final byte USB_WRITEABLE = 2;
    public static final byte USB_CONFIGURED = 0x10;
    public static final byte USB_UNCONFIGURED = 0x20;
    
    // Private versions of LCP values. We don't want to pull in all of the
    // LCP code.
    private static final byte SYSTEM_COMMAND_REPLY = 0x01;
	private static final byte REPLY_COMMAND = 0x02;
	private static final byte GET_FIRMWARE_VERSION = (byte)0x88;
	private static final byte GET_DEVICE_INFO = (byte)0x9B;
    private static final byte NXJ_PACKET_MODE = (byte)0xFF;
    
    // Events used for internal state changes
    static final int USB_DISCONNECT = NXTEvent.USER1;
    static final int USB_NEWDATA = NXTEvent.USER2;
    static final int USB_NEWSPACE = NXTEvent.USER3;

    static final int FLUSH_TIMEOUT = 100;

    // Internal synchronization object
    private static final Object usbSync = new Object();
    private static NXTEvent usbEvent;
    private static boolean listening = false;
    private static USBConnection connection;
    

    static {
        loadSettings();
    }

    static class USBThread extends Thread
    {
        public USBThread()
        {
            setDaemon(true);
            setPriority(MAX_PRIORITY);
        }

        /**
         * Main I/O thread for USB connections
         */
        @Override
        public void run()
        {
            try {
                while (true)
                {
                    // Wait for a connection to be established
                    synchronized(usbSync)
                    {
                        while (connection == null)
                            usbSync.wait();
                    }
                    // Perform I/O on the connection
                    while(connection != null)
                    {
                        int event = connection.send();
                        event |= connection.recv();
                        int ret = usbEvent.waitEvent(event|USB_DISCONNECT|USB_UNCONFIGURED, NXTEvent.WAIT_FOREVER);
                        if (ret < 0 || (ret & (USB_DISCONNECT|USB_UNCONFIGURED)) != 0) connection.disconnected();
                    }
                }
            }
            catch (InterruptedException e)
            {
                // time to give up
                return;
            }
            finally
            {
                if (connection != null)
                    connection.disconnected();
            }
        }
    }
    
	private USB()
	{
	    // empty
	}
   
    private static boolean isConnected(byte [] cmd)
    {
        // This method provides support for packet mode connections.
        // We wait for the PC to tell us that the connection has been established.
        // While waiting we support a small sub-set of LCP to allow identification
        // of the device.
        int len = 3;
        boolean ret = false;
        // Look for a system command
        if (usbRead(cmd, 0, cmd.length) >= 2 && cmd[0] == SYSTEM_COMMAND_REPLY)
        {
            cmd[2] = (byte)0xff;
            if (cmd[1] == GET_FIRMWARE_VERSION) 
            {
                cmd[2] = 0;
                cmd[3] = 2;
                cmd[4] = 1;
                cmd[5] = 3;
                cmd[6] = 1;			
                len = 7;
            }
		
            // GET DEVICE INFO
            if (cmd[1] == GET_DEVICE_INFO) 
            {
                cmd[2] = 0;
                // We only send back the device devName.
                for(int i=0;i<devName.length();i++) cmd[3+i] = (byte)devName.charAt(i);
                len = 33;
            }	
             // Switch to packet mode
            if (cmd[1] == NXJ_PACKET_MODE)
            {
                // Send back special signature to indicate we have accepted packet
                // mode
                cmd[1] = (byte)0xfe;
                cmd[2] = (byte)0xef;
                ret = true;
                len = 3;
            }
            cmd[0] = REPLY_COMMAND;
            usbWrite(cmd, 0, len);
        }
        return ret;
    }

    /**
     * Notify the I/O thread of an event
     * @param event
     */
    static void notifyEvent(int event)
    {
        usbEvent.notifyEvent(event);
    }

    
	/**
     * Wait for the USB interface to become available and for a PC side program
     * to attach to it.
     * @param timeout length of time to wait (in ms), if 0 wait for ever
     * @param mode The IO mode to be used for the connection. (see NXTConnection)
     * @return a connection object or null if no connection.
     */
    public static USBConnection waitForConnection(int timeout, int mode)
    {
        // Check the state of things and initialize if required
        synchronized(usbSync)
        {
            // We only allow a single connection over USB
            if (listening || connection != null) return null;
            if (usbEvent == null)
            {
                usbEvent = NXTEvent.allocate(NXTEvent.USB, 0, 1);
                USBThread usbThread = new USBThread();
                usbThread.start();
                // Add shutdown hook to clean up connections
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {if (connection != null) connection.close();}
                });
            }
            usbEvent.clearEvent(USB_DISCONNECT);
            listening = true;
        }
        // Now wait for a connection
        byte [] buf = new byte [HW_BUFSZ];
        //USB.usbEvent.waitEvent(USB.USB_WRITEABLE|USB.USB_UNCONFIGURED, NXTEvent.WAIT_FOREVER);
        usbSetName(devName);
        usbSetSerialNo(devAddress);
        usbEnable(((mode & RESET) != 0 ? 1 : 0));
        mode &= ~RESET;
        long end = (timeout == 0 ? 0x7fffffffffffffffL : System.currentTimeMillis() + timeout);
        while(listening)
        {
            int status;
            try {
                // Wait for the USB interface to become ready for use.
                status = usbEvent.waitEvent(USB_CONFIGURED|USB_DISCONNECT, end - System.currentTimeMillis());
                // check for timeout or cancel
                if ((status & (USB_DISCONNECT|NXTEvent.TIMEOUT)) != 0) break;
                // Interface is ready, for LCP and packet mode we wait for data to arrive
                if (mode == NXTConnection.LCP || mode == NXTConnection.PACKET)
                    status = usbEvent.waitEvent(USB_READABLE|USB_UNCONFIGURED|USB_DISCONNECT, end - System.currentTimeMillis());
            }
            catch(InterruptedException e)
            {
                // preserve state of interrupt flag
                Thread.currentThread().interrupt();
                // and give up
                break;
            }
            if ((status & (USB_DISCONNECT|NXTEvent.TIMEOUT)) != 0) break;
            if ((status & USB_UNCONFIGURED) != 0) continue;
            if (mode == NXTConnection.RAW ||
                (mode == NXTConnection.LCP && ((status & USB_READABLE) != 0)) ||
                (mode == NXTConnection.PACKET && isConnected(buf)))
            {
                synchronized(usbSync)
                {
                    // now connected, set things up for I/O
                    listening = false;
                    connection = new USBConnection(mode);
                    usbSync.notifyAll();
                    return connection;
                }
            }
        }
        // Failed to connect, clean things up
        freeConnection();
        return null;
    }

    /**
     * Disable the device and free associated resources.
     */
    static void freeConnection()
    {
        synchronized(usbSync)
        {
            listening = false;
            connection = null;
            try {
                // Wait for any output to drain
                usbEvent.waitEvent(USB_WRITEABLE|USB_UNCONFIGURED, FLUSH_TIMEOUT);
                // And give things chance to settle
                usbEvent.waitEvent(USB_UNCONFIGURED, FLUSH_TIMEOUT);
            }
            catch(InterruptedException e)
            {
                // preserve state of interrupt flag
                Thread.currentThread().interrupt();
                // and continue
            }
            usbDisable();
            usbSync.notifyAll();
        }
    }
    /**
     * Wait for ever for the USB connection to become available.
     * @return a connection object or null if no connection.
     */
    public static USBConnection waitForConnection()
    {
        return waitForConnection(0, 0);
    }
    

    /**
     * Cancel a long running command issued on another thread.
     * NOTE: Currently only the WaitForConnection calls can be cancelled.
     * @return true if the command was cancelled, false otherwise.
     */
    public static boolean cancelConnect()
    {
        synchronized(usbSync)
        {
            if (listening)
            {
                listening = false;
                usbEvent.notifyEvent(USB_DISCONNECT);
                try{usbSync.wait();}catch(InterruptedException e){/*empty*/}
                return true;
            }
        }
        return false;
    }
    
	public static native void usbEnable(int reset);
	public static native void usbDisable();
	public static native void usbReset();
	public static native int usbRead(byte [] buf, int off, int len);
	public static native int usbWrite(byte [] buf, int off, int len);
    public static native int usbStatus();
    public static native void usbSetSerialNo(String serNo);
    public static native void usbSetName(String name);


    /**
     * Class to provide polymorphic access to the connection methods.
     * Gets returned as a singleton by getConnector and can be used to create
     * connections.
     */
    static class Connector extends NXTCommConnector
    {
        /**
         * Open a connection to the specified name/address using the given I/O mode
         * @param target The name or address of the device/host to connect to.
         * @param mode The I/O mode to use for this connection
         * @return A NXTConnection object for the new connection or null if error.
         */
        public NXTConnection connect(String target, int mode)
        {
            return null;
        }

        /**
         * Wait for an incoming connection, or for the request to timeout.
         * @param timeout Time in ms to wait for the connection to be made
         * @param mode I/O mode to be used for the accepted connection.
         * @return A NXTConnection object for the new connection or null if error.
         */
        public NXTConnection waitForConnection(int timeout, int mode)
        {
            return USB.waitForConnection(timeout, mode);
        }

        /**
         * Cancel a connection attempt.
         * @return true if the connection attempt has been aborted.
         */
        public boolean cancel()
        {
            return USB.cancelConnect();
        }

    }
    
    static NXTCommConnector connector = null;

    /**
     * Provides access to the singleton connection object.
     * This object can be used to create new connections.
     * @return the connector object
     */
    public static NXTCommConnector getConnector()
    {
        if (connector == null)
            connector = new Connector();
        return connector;
    }
    
}
