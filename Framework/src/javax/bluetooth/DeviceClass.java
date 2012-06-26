package javax.bluetooth;

/**
 * The DeviceClass class represents the class of device (CoD) record as defined by the Bluetooth specification. This record is defined 
 * in the Bluetooth Assigned Numbers document and contains information on the type of the device and the type of services available on the device.
 * The Bluetooth Assigned Numbers document ( http://www.bluetooth.org/assigned-numbers/baseband.htm) defines the service class, major 
 * device class, and minor device class. The table below provides some examples of possible return values and their meaning:
 * <table border="1">
 * <tr>
 * <td>Method</td><td>Return Value</td><td>Class of Device</td>
 * </tr><tr>
 * <td>getServiceClasses()</td><td>0x22000</td><td>Networking and Limited Discoverable Major Service Classes</td>
 * </tr><tr>
 * <td>getServiceClasses()</td><td>0x100000</td><td>Object Transfer Major Service Class</td>
 * </tr><tr>
 * <td>getMajorDeviceClass()</td><td>0x00</td><td>Miscellaneous Major Device Class</td>
 * </tr><tr>
 * <td>getMajorDeviceClass()</td><td>0x200</td><td>Phone Major Device Class</td>
 * </tr><tr>
 * <td>getMinorDeviceClass()</td><td>0x0C</td><td>With a Computer Major Device Class, Laptop Minor Device Class</td>
 * </tr><tr>
 * <td>getMinorDeviceClass()</td><td>0x04</td><td>With a Phone Major Device Class, Cellular Minor Device Class</td>
 * </tr>
 * </table>
 * @author BB
 *
 */
public class DeviceClass {

	private static final int SERVICE_MASK = 0xffe000;
	private static final int MAJOR_MASK = 0x001f00;
	private static final int MINOR_MASK = 0x0000fc;
	
	int major;
	int minor;
	int service;
		
	/**
	 * 
	 * @param record
	 * 
	 * @exception IllegalArgumentException if <code>record</code> has any bits
     * between 24 and 31 set
	 */
	public DeviceClass(int record) {
		
		major = record & MAJOR_MASK; 
		minor = record & MINOR_MASK;
		service = record & SERVICE_MASK;
		
		// FIXME figure out why some devices have non-zero most significant byte
		// until then, don't throw an exception
		// if ((record & 0xff000000) != 0)
        //    throw new IllegalArgumentException();
	}
	
	/**
	 * Retrieves the major service classes. A device may have multiple major service classes. When this occurs, the major service classes are bitwise OR'ed together.
	 * @return the major service classes
	 */
	public int getServiceClasses() {
		return service;
	}

	// TODO: Implement DeviceClass.equals()? Not sure if other APIs define this.
	
	/**
	 * Retrieves the major device class. A device may have only a single major device class.
	 * @return the major device class
	 */
	public int getMajorDeviceClass() {
		return major;
	}
	
	public int getMinorDeviceClass() {
		return minor;
	}
}
