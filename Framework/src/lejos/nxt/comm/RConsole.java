package lejos.nxt.comm;

import lejos.nxt.*;
import java.io.*;
import lejos.util.Delay;
import lejos.nxt.debug.*;
import java.lang.InterruptedException;

/**
 * This class provides a simple way of sending output for viewing on a 
 * PC. The output is transmitted via the nxt USB connection or via Bluetooth.
 * If open is not called or if the connection to the PC is timed out, then
 * the output is discarded. The class may also be used to provide a remote view
 * of the NXT LCD display, and to capture various debug events. The use of these
 * facilities requires corresponding capabilities within the remote viewer and
 * is negotiated at connection time.
 *
 * Use of this class is normally initiated in two ways:<br>
 * 1. Explicit usage. The user program makes a call to one of the open methods
 *    and than uses the RConsole.println method to display output.<br>
 * 2. Implicit usage. In this mode the -gr (--remotedebug) is specified to the 
 *    leJOS linker and this arranges to start the user program via this class. 
 *    This class then hooks any required debug events, waits for a remote viewer
 *    to connect (via either Bluetooth or USB), and then routes the standard
 *    system output and err print streams to the remote display.
 *
 */
public class RConsole extends Thread
{
    /*
     * Developer notes
     * This code is used when the system is fully operational (normal mode) and
     * when it is running after a debug event has ocurred (debug mode). In this
     * later mode all users threads are suspended. This means that great care
     * must be taken to ensure that no locks can be held by user code that will
     * prevent the correct operation when in debug mode. In particular this
     * means that if Bluetooth is being use for the remote display, then the
     * user code should not also use Bluetooth.
     * 
     * To ensure correct operation we use two separate print streams one for
     * normal mode the other debug mode. This ensures that any locks held on the
     * normal print stream will not block debug mode I/O.
     */
    static final int OPT_LCD = 1;
    static final int OPT_EVENTS = 2;

    static final int IO_EVENT = NXTEvent.USER1;
    
    static final int MODE_SWITCH = 0xff;
    static final int MODE_LCD = 0x0;
    static final int MODE_EVENT = 0x1;
    
    static final int LCD_UPDATE_PERIOD = 100;
    
    static volatile PrintStream ps;
    static PrintStream psNormal;
    static PrintStream psDebug;
    static OutputStream os;
    static volatile byte[] output;
    static volatile int outputLen;
    static volatile NXTConnection conn;
    static RConsole ioThread;
    static NXTEvent ioEvent;
    static boolean lcd = false;
    static boolean events = false;

    /**
     * This internal class is used to provide a print stream connection to the
     * remote console. Note that to avoid locking issues between normal and
     * debug mode operation it is not connected directly to the remote output
     * stream. Instead an event scheme is used to pass data between
     * this code and the main output thread.
     */
    static private class RConsoleOutputStream extends OutputStream
    {
        private byte[] buffer;
        private int numBytes = 0;

        /**
         * Create an internal print stream for use by the remote console.
         * @param buffSize
         */
        RConsoleOutputStream(int buffSize)
        {
            buffer = new byte[buffSize];
            output = null;
        }

        /**
         * Write data to the stream flush when full.
         * @param b the byte to write
         * @throws IOException
         */
        public synchronized void write(int b) throws IOException
        {
            if (numBytes == buffer.length)
            {
                flush();
            }
            buffer[numBytes] = (byte) b;
            numBytes++;
        }
        /**
         * Flush the data to the remote console stream. 
         * @throws IOException
         */
        @Override
        public synchronized void flush() throws IOException
        {
            if (numBytes > 0)
            {
                // use busy wait synchronization.
                outputLen = numBytes;
                output = buffer;
                numBytes = 0;
                if (conn != null)
                {
                    /*
                     * We need to wake up the I/O thread but we must take care
                     * with holding a lock. The really safe way to do this is to
                     * use interrupt. However this will generate an exception
                     * which means a stack trace will be captures which costs
                     * time and creates garbage. So instead we can use internal
                     * knowledge of the leJOS scheduler. Following a yield this
                     * thread will run for at least 1ms without any task switch
                     * this means that it is is pretty safe to take the lock and
                     * use notify (which will not switch away), to wake up the
                     * other thread...
                     */
                    ioEvent.notifyEvent(IO_EVENT);
                    while (output != null)
                        Thread.yield();
                    /*
                    buffer[0] = (byte)outputLen;
                    if (USB.usbWrite(buffer, 0, outputLen+2) <= 0)
                    {
                        blocked++;
                    while(USB.usbWrite(buffer, 0, outputLen+2) <= 0)
                        Thread.yield();
                    }*/
                    //ioThread.interrupt();
                    /*
                    Thread.yield();
                    synchronized(os)
                    {
                        os.notify();
                    }
                    while (output != null)
                        Thread.yield();
                     *
                     */
                }
            }
           
        }
    }

    /**
     * Ensure that this class is never instantiated.
     */
    private RConsole()
    {
    }

    /**
     * Setup the remote connection. Perform the standard handshake and setup
     * the connection ready to go.
     * @param c The connection to use for the remote console.
     */
    private static void init(NXTConnection c)
    {
        if (c == null)
            return;
        try
        {
            LCD.drawString("Got connection  ", 0, 0);
            // Perfomr the handshake. This conists of 2 signature bytes 'RC'
            // followed by a single capability byte.
            byte[] hello = new byte[32];
            int len = c.read(hello, hello.length);
            if (len != 3 || hello[0] != 'R' || hello[1] != 'C')
            {
                LCD.drawString("Console no h/s    ", 0, 0);
                c.close();
                return;
            }
            LCD.drawString("Console open    ", 0, 0);
            conn = c;
            os = conn.openOutputStream();
            ps = psNormal = new PrintStream(new RConsoleOutputStream(128));
            LCD.refresh();
            lcd = ((hello[2] & OPT_LCD) != 0);
            events = ((hello[2] & OPT_EVENTS) != 0);
            ioEvent = NXTEvent.allocate(NXTEvent.NONE, IO_EVENT, 5);
            // Create the I/O thread and start it.
            ioThread = new RConsole();
            ioThread.setPriority(Thread.MAX_PRIORITY);
            ioThread.setDaemon(true);
            ioThread.start();
            println("Console open");
        } catch (Exception e)
        {
            LCD.drawString("Console error " + e.getMessage(), 0, 0);
            LCD.refresh();
        }
    }


    /**
     * Wait for a remote viewer to connect via USB.
     * @param timeout how long to wait, 0 waits for ever.
     */
    public static void openUSB(int timeout)
    {
        LCD.drawString("USB Console...  ", 0, 0);
        init(USB.waitForConnection(timeout, 0));

    }

    /**
     * Wait for a remote viewer to connect via Bluetooth.
     * @param timeout how long to wait, 0 waits for ever.
     */
    public static void openBluetooth(int timeout)
    {
        LCD.drawString("BT Console...   ", 0, 0);
        init(Bluetooth.waitForConnection(timeout, NXTConnection.PACKET, null));
    }

    /**
     * Internal thread used to wait for a connection.
     */
    private static class ConnectThread extends Thread
    {

        ConnectThread other;
        boolean finished = false;
    }

    /**
     * Wait for a remote viewer to connect using either USB or Bluetooth.
     * @param timeout time to wait for the connection, 0 waits for ever.
     */
    public static void openAny(final int timeout)
    {
        // Bluetooth connection thread
        ConnectThread btThread = new ConnectThread()
        {

            @Override
            public void run()
            {
                openBluetooth(timeout);
                // force the other connect to stop
                finished = true;
                while (!other.finished)
                    USB.cancelConnect();
            }
        };
        // USB connection thread.
        ConnectThread usbThread = new ConnectThread()
        {

            @Override
            public void run()
            {
                openUSB(timeout);
                finished = true;
                while (!other.finished)
                    Bluetooth.cancelConnect();
            }
        };
        btThread.other = usbThread;
        usbThread.other = btThread;
        btThread.start();
        usbThread.start();
        Delay.msDelay(10);
        LCD.drawString("Remote Console...   ", 0, 0);
        try
        {
            btThread.join();
            usbThread.join();
        } catch (InterruptedException e)
        {
        }
    }

    /**
     * Wait forever for a remote viewer to connect.
     */
    public static void open()
    {
        openAny(0);
    }

    /**
     * Send output to the remote viewer.
     * @param s
     */
    public static void print(String s)
    {
        if (conn == null)
            return;
        ps.print(s);
        ps.flush();
    }

    /**
     * Send a line to the remote viewer.
     * @param s
     */
    public static void println(String s)
    {
        if (conn == null)
            return;
        ps.println(s);
    }

    /**
     * Close the remote console connection.
     */
    public static void close()
    {
        if (conn == null) return;
        println("Console closed");
        synchronized (os)
        {
            try
            {
                conn.close();
                conn = null;
                LCD.drawString("Console closed  ", 0, 0);
                LCD.refresh();
                Delay.msDelay(2000);
            } catch (Exception e)
            {
            }
        }
    }

    /**
     * Check to see if the remote console is available for use.
     * @return true if the console is open.
     */
    public static boolean isOpen()
    {
        return (conn != null);
    }

    /**
     * Return a print stream connected to the remote console.
     * @return the print stream
     */
    public static PrintStream getPrintStream()
    {
        return ps;
    }

    /**
     * Main console I/O thread.
     */
    @Override
    public void run()
    {
        try {
            long nextUpdate = 0;
            while (true)
            {
                long now = System.currentTimeMillis();
                synchronized (os)
                {
                    if (conn == null)
                        break;
                    try
                    {
                        // First check to see if we have any "normal" output to go.
                        if (output != null)
                        {
                            os.write(output, 0, outputLen);
                            output = null;
                            os.flush();
                        }
                        // Are we mirroring the LCD display?
                        if (lcd)
                        {
                            if (now > nextUpdate)
                            {
                                os.write(MODE_SWITCH);
                                os.write(MODE_LCD);
                                os.write(LCD.getDisplay());
                                os.flush();
                                nextUpdate = now + LCD_UPDATE_PERIOD;
                            }
                        }
                        else
                            nextUpdate = now + LCD_UPDATE_PERIOD;
                    } catch (Exception e)
                    {
                        // Not really sure what do if we get an I/O error. Should
                        // probably have some way to report it to calling threads.
                        break;
                    }
                    /*
                    try {
                        os.wait(nextUpdate - now);
                    }
                    catch (InterruptedException e)
                    {
                        // We may use interrupt to wake the thread from sleep early.
                    }*/
                }
                ioEvent.waitEvent(nextUpdate - now);
            }
            // dump any pending output
            output = null;
        }
        catch(InterruptedException e)
        {
            // must have been aborted, so exit
            return;
        }
    }

    /**
     * Send an exception event to the remote console.
     * @param classNo The exception class.
     * @param methodNo The method in which the exception occurred.
     * @param pc The location of the exception.
     * @param stackTrace An internal stack trace.
     * @param msg A text message associated with the exception.
     * @return True if OK.
     */
    public static boolean exception(int classNo, int methodNo, int pc, int[] stackTrace, String msg)
    {
        if (conn != null && events)
        {
            synchronized (os)
            {
                try
                {
                    os.write(MODE_SWITCH);
                    os.write(MODE_EVENT);
                    os.write(classNo);
                    if (msg == null)
                        msg = "";
                    os.write(msg.length());
                    os.write(0);
                    for (char ch : msg.toCharArray())
                        os.write(ch);
                    if (stackTrace == null)
                        os.write(0);
                    else
                    {
                        os.write(stackTrace.length);
                        for (int frame : stackTrace)
                        {
                            for (int i = 0; i < 4; i++)
                            {
                                os.write(frame);
                                frame >>>= 8;
                            }
                        }
                    }
                    os.flush();
                } catch (IOException e)
                {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Set the stream to be used for output. Used to switch between normal
     * mode and debug mode
     * @param p the PrintStream to use for RConsole I/O
     */
    static private void setStreams(PrintStream p)
    {
        ps = p;
        System.setErr(p);
        System.setOut(p);

    }

    /**
     * The following internal class provides an implicit remote console and
     * also hooks into the debug event system.
     */
    static public class Monitor extends DebugMonitor
    {
        static PrintStream debugPS;

        /**
         * Exit the program
         */
        @Override
        protected void exit()
        {
            RConsole.close();
            super.exit();
        }

        /**
         * Handle a debug event.
         * @param event
         */
        @Override
        protected void processEvent(int event)
        {
            // Switch to debug streams
            setStreams(psDebug);
            switch (event)
            {
                case DebugInterface.DBG_EXCEPTION:
                    if (!exception(VM.getVM().getVMClass(monitor.exception).getClassNo(),
                            monitor.method, monitor.pc,
                            VM.getThrowableStackTrace(monitor.exception), monitor.exception.getMessage()))
                        displayException(monitor);
                    exit();
                    break;
                case DebugInterface.DBG_USER_INTERRUPT:
                    System.err.println("User interrupt");
                case DebugInterface.DBG_PROGRAM_EXIT:
                    System.err.println("Program exit");
                    exit();
                    break;
            }
            // Switch back...
            setStreams(psNormal);
        }

        /**
         * Wait for the viewer to connect, and then run and monitor the user
         * program.
         * @param args
         * @throws Exception
         */
        public static void main(String[] args) throws Exception
        {
            // Open the console and re-direct standard channels to it.
            RConsole.open();
            // Create print stream for use during debug events.
            psDebug = new PrintStream(new RConsoleOutputStream(128));
            // and set things into normal mode
            setStreams(psNormal);
            // now create and run the monitor.
            new Monitor().monitorEvents();
        }
    }
}


