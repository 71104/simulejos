package javax.microedition.location;

/**
 * The Location class represents the standard set of basic location information. This
 * includes the timestamped coordinates, accuracy, speed, course, and information about
 * the positioning method used for the location, plus an optional textual address.
 * <p>
 * The location method is indicated using a bit field. The individual bits are defined
 * using constants in this class. This bit field is a bitwise combination of the location
 * method technology bits (MTE_*), method type (MTY_*) and method assistance information
 * (MTA_*). All other bits in the 32 bit integer than those that have defined constants in
 * this class are reserved and MUST not be set by implementations (i.e. these bits must be
 * 0).
 * <p>
 * A Location object may be either 'valid' or 'invalid'. The validity can be queried using
 * the isValid method. A valid Location object represents a location with valid
 * coordinates and the getQualifiedCoordinates method must return there coordinates. An
 * invalid Location object doesn't have valid coordinates, but the extra info that is
 * obtained from the getExtraInfo method can provide information about the reason why it
 * was not possible to provide a valid Location. For an invalid Location object, the
 * getQualifiedCoordinates method may return either null or some coordinates where the
 * information is not necessarily fully correct. The periodic location updates to the
 * LocationListener may return invalid Location objects if it isn't possible to determine
 * the location.
 * <p>
 * This class is only a container for the information. When the platform implementation
 * returns Location objects, it MUST ensure that it only returns objects where the
 * parameters have values set as described for their semantics in this class.
 */
public class Location {

	private float speed = -1;
	private float course = -1;
	private long timeStamp = -1;
	private String extraInfo = null;
	private int locationMethod = -1;
	private QualifiedCoordinates qc = null;
	
	/**
	 * Location method is assisted by the other party (Terminal assisted for Network
	 * based, Network assisted for terminal based). MTA_ASSISTED = 0x00040000
	 */
	public static final int MTA_ASSISTED = 0x00040000;

	/**
	 * Location method is unassisted. This bit and MTA_ASSISTED bit MUST NOT both be set.
	 * Only one of these bits may be set or neither to indicate that the assistance
	 * information is not known. MTA_UNASSISTED = 0x00080000
	 */
	public static final int MTA_UNASSISTED = 0x00080000;

	/**
	 * Location method Angle of Arrival for cellular / terrestrial RF system.
	 * MTE_ANGLEOFARRIVAL = 0x00000020
	 */
	public static final int MTE_ANGLEOFARRIVAL = 0x00000020;

	/**
	 * Location method Cell-ID for cellular (in GSM, this is the same as CGI, Cell Global
	 * Identity). MTE_CELLID = 0x00000008
	 */
	public static final int MTE_CELLID = 0x00000008;

	/**
	 * Location method using satellites (for example, Global Positioning System (GPS)).
	 * MTE_SATELLITE = 0x00000001
	 */
	public static final int MTE_SATELLITE = 0x00000001;

	/**
	 * Location method Short-range positioning system (for example, Bluetooth LP).
	 * MTE_SHORTRANGE = 0x00000010
	 */
	public static final int MTE_SHORTRANGE = 0x00000010;

	/**
	 * Location method Time Difference for cellular / terrestrial RF system (for example,
	 * Enhanced Observed Time Difference (E-OTD) for GSM). MTE_TIMEDIFFERENCE = 0x00000002
	 */
	public static final int MTE_TIMEDIFFERENCE = 0x00000002;

	/**
	 * Location method Time of Arrival (TOA) for cellular / terrestrial RF system.
	 * MTE_TIMEOFARRIVAL = 0x00000004
	 */
	public static final int MTE_TIMEOFARRIVAL = 0x00000004;

	/**
	 * Location method is of type network based. This means that the final location result
	 * is calculated in the network. This bit and MTY_TERMINALBASED bit MUST NOT both be
	 * set. Only one of these bits may be set or neither to indicate that it is not known
	 * where the result is calculated. MTY_NETWORKBASED = 0x00020000
	 */
	public static final int MTY_NETWORKBASED = 0x00020000;

	/**
	 * Location method is of type terminal based. This means that the final location
	 * result is calculated in the terminal. MTY_TERMINALBASED = 0x00010000
	 */
	public static final int MTY_TERMINALBASED = 0x00010000;
	
	/**
	 * A protected constructor for the Location to allow implementations of
	 * LocationProviders to construct the Location instances. This method is not intended
	 * to be used by applications.
	 * <p>
	 * This constructor sets the fields to implementation specific default values.
	 * Location providers are expected to set the fields to the correct values after
	 * constructing the object instance.
	 * @throws IllegalArgumentException
	 *             if the speed is negative
	 *             if the course is outside 0.0 to 360.0
	 */
	protected Location(QualifiedCoordinates coords, float speed, float course,
			long timeStamp, int locationMethod, String extraInfo) {
		this.qc = coords;
		setSpeed(speed);
		setCourse(course);
		this.timeStamp = timeStamp;
		this.locationMethod = locationMethod;
		this.extraInfo = extraInfo;
	}
	
	/**
	 * Returns the AddressInfo associated with this Location object. If no address is
	 * available, null is returned. In leJOS NXJ, null is always returned.
	 *
	 * @return an AddressInfo associated with this Location object
	 */
	// TODO: Probably no need to really implement this method.
	/*public AddressInfo getAddressInfo() {
		return null;
	}*/

	/**
	 *     Returns the AddressInfo associated with this Location object. 
	 *     If no address is available, null is returned.
	 *     In leJOS this currently always returns null.
	 *     @return an AddressInfo associated with this Location object
	 */
	public AddressInfo getAddressInfo() {
		/* TODO: I don't think this can be implemented in leJOS NXJ
		 * unless it had access to address information from a database.
		 * 
		 */
		return null;
	}
	
	/**
	 * Returns the terminal's course made good in degrees relative to true north. The
	 * value is always in the range [0.0,360.0) degrees.
	 *
	 * @return the terminal's course made good in degrees relative to true north or
	 *         Float.NaN if the course is not known
	 */
	public float getCourse() {
		return course;
	}

	/**
	 * This method is not implemented by leJOS and returns null every time. 
	 * 
	 * @param mimetype
	 *            This variable is ignored by leJOS NXJ. You can submit null.
	 * @return Nothing.
	 */
	public String getExtraInfo(String mimetype) {
		return extraInfo;
	}

	/**
	 * Returns information about the location method used. The returned value is a bitwise
	 * combination (OR) of the method technology, method type and assistance information.
	 * The method technology values are defined as constant values named MTE_* in this
	 * class, the method type values are named MTY_* and assistance information values are
	 * named MTA_*. For example, if the location method used is terminal based, network
	 * assisted E-OTD, the value 0x00050002 ( = MTY_TERMINALBASED | MTA_ASSISTED |
	 * MTE_TIMEDIFFERENCE) would be returned.
	 * <p>
	 * If the location is determined by combining several location technologies, the
	 * returned value may have several MTE_* bits set.
	 * <p>
	 * If the used location method is unknown, the returned value must have all the bits
	 * set to zero.
	 * <p>
	 * Only bits that have defined constants within this class are allowed to be used.
	 * Other bits are reserved and must be set to 0.
	 *
	 * @return a bitfield identifying the used location method
	 */
	public int getLocationMethod() {
		return locationMethod;
	}

	/**
	 * Returns the coordinates of this location and their accuracy.
	 *
	 * @return a QualifiedCoordinates object. If the coordinates are not known, returns
	 *         null.
	 */
	public QualifiedCoordinates getQualifiedCoordinates() {
		return qc;
	}

	/**
	 * Returns the terminal's current ground speed in meters per second (m/s) at the time
	 * of measurement. The speed is always a non-negative value. Note that unlike the
	 * coordinates, speed does not have an associated accuracy because the methods used to
	 * determine the speed typically are not able to indicate the accuracy.
	 *
	 * @return the current ground speed in m/s for the terminal or Float.NaN if the speed
	 *         is not known
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Returns the time stamp at which the data was collected. This timestamp should
	 * represent the point in time when the measurements were made. Implementations make
	 * best effort to set the timestamp as close to this point in time as possible. The
	 * time returned is the time of the local clock in the terminal in milliseconds using
	 * the same clock and same time representation as System.currentTimeMillis().
	 *
	 * @return a timestamp representing the time
	 */
	public long getTimestamp() {
		return timeStamp;
	}

	/**
	 * Returns whether this Location instance represents a valid location with coordinates
	 * or an invalid one where all the data, especially the latitude and longitude
	 * coordinates, may not be present. A valid Location object contains valid coordinates
	 * whereas an invalid Location object may not contain valid coordinates but may
	 * contain other information via the getExtraInfo() method to provide information on
	 * why it was not possible to provide a valid Location object.
	 *
	 * @return a boolean value with true indicating that this Location instance is valid
	 *         and false indicating an invalid Location instance
	 * @see #getExtraInfo(String)
	 */
	public boolean isValid() {
		// TODO: qc test is not very good. Should look at data.
		if(course < 0|speed < 0|qc == null)
			return false;
		else
			return true;
	}

	/**
	 * Sets the terminal's course, ensuring that the value is always in the range
	 * [0.0,360.0) degrees.
	 *
	 * @param course
	 *            the course to set.
	 * @throws IllegalArgumentException
	 *             if the course is outside the range.
	 */
	protected void setCourse(float course) throws IllegalArgumentException {
		if(course < 0|course > 360) throw new IllegalArgumentException("course outside 0-360");
		this.course = course;
	}

	/**
	 * Set the current ground speed of the location object in m/s.
	 *
	 * @param speed
	 *            must be non-negative or Float.NaN if unknown.
	 * @throws IllegalArgumentException
	 *             if the speed is negative
	 */
	protected void setSpeed(float speed) throws IllegalArgumentException {
		if(speed < 0) throw new IllegalArgumentException("speed can't be negative");
		this.speed = speed;
	}
}
