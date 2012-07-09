package javax.bluetooth;

import java.util.ArrayList;

import lejos.nxt.comm.Bluetooth;

/**
 * The <code>DiscoveryAgent</code> class provides methods to perform device
 * discovery (but not service discovery in leJOS NXJ). A local device must have only one
 * <code>DiscoveryAgent</code> object. This object must be retrieved by a call
 * to <code>getDiscoveryAgent()</code> on the <code>LocalDevice</code>
 * object.
 *
 * The three service methods normally in the DiscoveryAgent class such as searchServices()
 * are not included because the Lego NXT brick only allows one service: SPP (Serial Port
 * Profile). It would waste memory to implement the service methods considering they are not
 * really functional. 
 *
 * <H3>Device Discovery</H3>
 *
 * There are two ways to discover devices. First, an application may use
 * <code>startInquiry()</code> to start an inquiry to find devices in
 * proximity to the local device. Discovered devices are returned via the
 * <code>deviceDiscovered()</code> method of the interface
 * <code>DiscoveryListener</code>. The second way to discover devices is via
 * the <code>retrieveDevices()</code> method. This method will return devices
 * that have been discovered via a previous inquiry or devices that are
 * classified as pre-known. (Pre-known devices are those devices that are
 * defined in the Bluetooth Control Center as devices this device frequently
 * contacts.) The <code>retrieveDevices()</code> method does not perform an
 * inquiry, but provides a quick way to get a list of devices that may be in the
 * area.
 *
 * WARNING: If a device is found that has not yet been paired with the NXT brick, the name 
 * field of RemoteDevice will be blank. Make sure to pair your devices through the leJOS 
 * NXJ Bluetooth menu on your NXT.
 *
 * @author BB
 * @version 1.0 January 17, 2009
 *
 */
public class DiscoveryAgent {

        /**
         * Takes the device out of discoverable mode.
         * <P>
         * The value of <code>NOT_DISCOVERABLE</code> is 0x00 (0).
         */
        public static final int NOT_DISCOVERABLE = 0;

        /**
         * The inquiry access code for General/Unlimited Inquiry Access Code (GIAC).
         * This is used to specify the type of inquiry to complete or respond to.
         * <P>
         * The value of <code>GIAC</code> is 0x9E8B33 (10390323). This value is
         * defined in the Bluetooth Assigned Numbers document.
         * @see #startInquiry
         */
        public static final int GIAC = 0x9E8B33;

        /**
         * The inquiry access code for Limited Dedicated Inquiry Access Code (LIAC).
         * This is used to specify the type of inquiry to complete or respond to.
         * <P>
         * The value of <code>LIAC</code> is 0x9E8B00 (10390272). This value is
         * defined in the Bluetooth Assigned Numbers document.
         * @see #startInquiry
         */
        public static final int LIAC = 0x9E8B00;

        /**
         * Used with the <code>retrieveDevices()</code> method to return those
         * devices that were found via a previous inquiry. If no inquiries have been
         * started, this will cause the method to return <code>null</code>.
         * <P>
         * The value of <code>CACHED</code> is 0x00 (0).
         *
         * @see #retrieveDevices
         */
        public static final int CACHED = 0x00;

        /**
         * Used with the <code>retrieveDevices()</code> method to return those
         * devices that are defined to be pre-known devices. Pre-known devices are
         * specified in the BCC. These are devices that are specified by the user as
         * devices with which the local device will frequently communicate.
         * <P>
         * The value of <code>PREKNOWN</code> is 0x01 (1).
         *
         * @see #retrieveDevices
         */
        public static final int PREKNOWN = 0x01;
        
        /**
         * Use <code>LocalDevice.getDiscoveryAgent()</code> to get an object.
         * Prevents user from creating their own <code>DiscoveryAgent</code> object.
         */
        DiscoveryAgent() {
        }
        
        /**
         * Returns an array of Bluetooth devices that have either been found by the
         * local device during previous inquiry requests or been specified as a
         * pre-known device depending on the argument. The list of previously found
         * devices is maintained by the implementation of this API. (In other words,
         * maintenance of the list of previously found devices is an implementation
         * detail.) A device can be set as a pre-known device in the Bluetooth
         * Control Center.
         *
         * @param option
         *            <code>CACHED</code> if previously found devices should be
         *            returned; <code>PREKNOWN</code> if pre-known devices should
         *            be returned
         *
         * @return an array containing the Bluetooth devices that were previously
         *         found if <code>option</code> is <code>CACHED</code>; an
         *         array of devices that are pre-known devices if
         *         <code>option</code> is <code>PREKNOWN</code>;
         *         <code>null</code> if no devices meet the criteria
         *
         * @exception IllegalArgumentException
         *                if <code>option</code> is not <code>CACHED</code> or
         *                <code>PREKNOWN</code>
         */
        public RemoteDevice[] retrieveDevices(int option) {
            // TODO: For now it doesn't discern between CACHED or PREKNOWN as our
            // leJOS Bluetooth stack doesn't support this?
            // if(option == CACHED|option == PREKNOWN)
        	ArrayList<RemoteDevice> v = Bluetooth.getKnownDevicesList();
            RemoteDevice [] rdlist = new RemoteDevice[v.size()];
            for(int i=0;i<rdlist.length;i++)
            	rdlist[i] = v.get(i);
        	return rdlist;
        }

        /**
         * Places the device into inquiry mode. The length of the inquiry is
         * implementation dependent. This method will search for devices with the
         * specified inquiry access code. Devices that responded to the inquiry are
         * returned to the application via the method
         * <code>deviceDiscovered()</code> of the interface
         * <code>DiscoveryListener</code>. The <code>cancelInquiry()</code>
         * method is called to stop the inquiry.
         * NOTE: If a device is found that has not yet been paired with the NXT brick, 
         * the name field of RemoteDevice will be blank. Make sure to pair your devices 
         * through the leJOS NXJ Bluetooth menu on your NXT.
         *
         * @see #cancelInquiry
         * @see #GIAC
         * @see #LIAC
         *
         * @param accessCode
         *            the type of inquiry to complete
         *
         * @param listener
         *            the event listener that will receive device discovery events
         *
         * @return <code>true</code> if the inquiry was started;
         *         <code>false</code> if the inquiry was not started because the
         *         <code>accessCode</code> is not supported
         *
         * @exception IllegalArgumentException
         *                if the access code provided is not <code>LIAC</code>,
         *                <code>GIAC</code>, or in the range 0x9E8B00 to 0x9E8B3F
         *
         * @exception NullPointerException
         *                if <code>listener</code> is <code>null</code>
         *
         * @exception BluetoothStateException
         *                if the Bluetooth device does not allow an inquiry to be
         *                started due to other operations that are being performed
         *                by the device
         */
        public boolean startInquiry(int accessCode, DiscoveryListener listener) throws BluetoothStateException {
            final DiscoveryListener listy = listener;
        	if (listener == null) {
                    throw new NullPointerException();
            }
            if ((accessCode != LIAC) && (accessCode != GIAC) && ((accessCode < 0x9E8B00) || (accessCode > 0x9E8B3F))) {
                    throw new IllegalArgumentException("Invalid accessCode " + accessCode);
            }
            
            // TODO: In the Bluetooth.inquireNotify() method:
            // 1. Should probably NOT be daemon threads
            // 2. Spawn new thread for each notify? Need to check the JSR spec.
            
            // Spawn a separate thread to notify so it returns immediately:
			Thread t = new Thread() {
				public void run() {
					// !! 5 x 1.28 = 6.4 second timeout long enough? Seems good.
					final int MY_TIME_OUT = 7;
					// !! Only finds 10 devices max at present. Good enough?
					final int MAX_DEVICES = 10;
					Bluetooth.inquireNotify(MAX_DEVICES, MY_TIME_OUT, listy);
				}
			};
			// Daemon thread?
			t.setDaemon(true);
			t.start();
			
            return true;
        }


        /**
         * Removes the device from inquiry mode.
         * <P>
         * An <code>inquiryCompleted()</code> event will occur with a type of
         * <code>INQUIRY_TERMINATED</code> as a result of calling this method.
         * After receiving this event, no further <code>deviceDiscovered()</code>
         * events will occur as a result of this inquiry.
         *
         * <P>
         *
         * This method will only cancel the inquiry if the <code>listener</code>
         * provided is the listener that started the inquiry.
         *
         * @param listener
         *            the listener that is receiving inquiry events
         *
         * @return <code>true</code> if the inquiry was canceled; otherwise
         *         <code>false</code> if the inquiry was not canceled or if the
         *         inquiry was not started using <code>listener</code>
         *
         * @exception NullPointerException
         *                if <code>listener</code> is <code>null</code>
         */
        public boolean cancelInquiry(DiscoveryListener listener) {
                if (listener == null) {
                        throw new NullPointerException();
                }
                // Return true/false value from Bluetooth class
                return Bluetooth.cancelInquiry();
        }
}
