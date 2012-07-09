package javax.bluetooth;

/**
 * The <code>DiscoveryListener</code> interface allows an application to
 * receive device discovery (but not service discovery) events. The reason for omitting 
 * service discovery is because NXT only allows one service: SPP (Serial Port Profile). 
 * It would be possible to implement the two service discovery methods but it would be
 * complicated, require more memory usage (ServiceRecord and DataElement) and is not very
 * useful. This interface provides two methods for discovering devices.
 *
 * @version 1.0 November 30, 2008
 *
 * The following DiscoveryListener methods SHOULD return immediately :
 * <ul>
 * <li>DiscoveryListener.deviceDiscovered</li>
 * <li>DiscoveryListener.inquiryCompleted</li>
 * </ul>
 *
 */
public interface DiscoveryListener {

        /**
         * Indicates the normal completion of device discovery. Used with the
         * {@link #inquiryCompleted(int)} method.
         * <p>
         * The value of INQUIRY_COMPLETED is 0x00 (0).
         *
         * @see #inquiryCompleted(int)
         * @see DiscoveryAgent#startInquiry(int, javax.bluetooth.DiscoveryListener)
         */
        public static final int INQUIRY_COMPLETED = 0x00;

        /**
         * Indicates device discovery has been canceled by the application and did
         * not complete. Used with the {@link #inquiryCompleted(int)} method.
         * <p>
         * The value of INQUIRY_TERMINATED is 0x05 (5).
         *
         * @see  #inquiryCompleted(int)
         * @see DiscoveryAgent#startInquiry(int, javax.bluetooth.DiscoveryListener)
         * @see DiscoveryAgent#cancelInquiry(javax.bluetooth.DiscoveryListener)
         */
        public static final int INQUIRY_TERMINATED = 0x05;

        /**
         * Indicates that the inquiry request failed to complete normally, but was
         * not canceled.
         * <p>
         * The value of INQUIRY_ERROR is 0x07 (7).
         *
         * @see  #inquiryCompleted(int)
         * @see DiscoveryAgent#startInquiry(int, javax.bluetooth.DiscoveryListener)
         */
        public static final int INQUIRY_ERROR = 0x07;

        /*
         * TODO: ServiceRecord interface.
         * 
         * TODO:
         * Called when service(s) are found during a service search.
         * void	servicesDiscovered(int transID, ServiceRecord[] servRecord)
         * 
         * TODO:
         * Called when a service search is completed or was terminated because of an error.
         * void serviceSearchCompleted(int transID, int respCode)
         */
        
        /**
         * Called when a device is found during an inquiry. An inquiry searches for
         * devices that are discoverable. NOTE: If a device is found that has not yet
         * been paired with the NXT brick, the name field of RemoteDevice will be blank.
         * Make sure to pair your devices through the leJOS NXJ Bluetooth menu on your NXT.
         *
         * @param btDevice the device that was found during the inquiry
         * @param cod - the service classes, major device class, and minor device
         * class of the remote device
         * @see DiscoveryAgent#startInquiry(int, javax.bluetooth.DiscoveryListener)
         */
        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod);

        /**
         * Called when an inquiry is completed. The {@code discType} will be
         * {@link #INQUIRY_COMPLETED} if the inquiry ended normally or {@link #INQUIRY_TERMINATED}
         * if the inquiry was canceled by a call to
         * {@link DiscoveryAgent#cancelInquiry(DiscoveryListener)}. The {@code discType} will be
         * {@link #INQUIRY_ERROR} if an error occurred while processing the inquiry causing the
         * inquiry to end abnormally.
         *
         * @param discType the type of request that was completed; either
         *                              {@link #INQUIRY_COMPLETED}, {@link #INQUIRY_TERMINATED},
         *                              or {@link #INQUIRY_ERROR}
         * @see #INQUIRY_COMPLETED
         * @see #INQUIRY_TERMINATED
         * @see #INQUIRY_ERROR
         */
        public void inquiryCompleted(int discType);
}
