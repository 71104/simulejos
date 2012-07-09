package javax.microedition.location;

/**
 * The Landmark class represents a landmark, i.e. a known location with a name. A landmark
 * has a name by which it is known to the end user, a textual description,
 * QualifiedCoordinates and optionally AddressInfo.
 * <p>
 * This class is only a container for the information. The constructor does not validate
 * the parameters passed in but just stores the values, except the name field is never
 * allowed to be null. The get* methods return the values passed in the constructor or if
 * the values are later modified by calling the set* methods, the get* methods return the
 * modified values. The QualifiedCoordinates object inside the landmark is a mutable
 * object and the Landmark object holds only a reference to it. Therefore, it is possible
 * to modify the QualifiedCoordinates object inside the Landmark object by calling the
 * set* methods in the QualifiedCoordinates object. However, any such dynamic
 * modifications affect only the Landmark object instance, but MUST not automatically
 * update the persistent landmark information in the landmark store. The
 * LandmarkStore.updateLandmark method is the only way to commit the modifications to the
 * persistent landmark store.
 * <p>
 * When the platform implementation returns Landmark objects, it MUST ensure that it only
 * returns objects where the parameters have values set as described for their semantics
 * in this class.
 */
public class Landmark {

	/**
	 * @see #setAddressInfo(AddressInfo)
	 * @see #getAddressInfo()
	 */
	private AddressInfo addressInfo;

	/**
	 * @see #setQualifiedCoordinates(QualifiedCoordinates)
	 * @see #getQualifiedCoordinates()
	 */
	private QualifiedCoordinates coordinates;

	/**
	 * @see #setDescription(String)
	 * @see #getDescription()
	 */
	private String description;

	/**
	 * @see #setName(String)
	 * @see #getName()
	 */
	private String name;

	/**
	 * Constructs a new Landmark object with the values specified.
	 *
	 * @param name
	 *            the name of the landmark
	 * @param description
	 *            description of the landmark. May be null if not available.
	 * @param coordinates
	 *            the Coordinates of the landmark. May be null if not known.
	 * @param addressInfo
	 *            the textual address information of the landmark. May be null if not
	 *            known.
	 * @throws NullPointerException
	 *             if the name is null
	 */
	public Landmark(String name, String description,
			QualifiedCoordinates coordinates, AddressInfo addressInfo)
			throws NullPointerException {
		setName(name);
		setDescription(description);
		setQualifiedCoordinates(coordinates);
		setAddressInfo(addressInfo);
	}

	/**
	 * Gets the AddressInfo of the landmark.
	 *
	 * @return the AddressInfo of the landmark.
	 * @see #setAddressInfo(AddressInfo)
	 */
	public AddressInfo getAddressInfo() {
		return addressInfo;
	}

	/**
	 * Gets the landmark description.
	 *
	 * @return the description of the landmark, null if not available.
	 * @see #setDescription(String)
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the landmark name.
	 *
	 * @return the name of the landmark.
	 * @see #setName(String)
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the QualifiedCoordinates of the landmark.
	 *
	 * @return the QualifiedCoordinates of the landmark. null if not available.
	 * @see #setQualifiedCoordinates(QualifiedCoordinates)
	 */
	public QualifiedCoordinates getQualifiedCoordinates() {
		return coordinates;
	}

	/**
	 * Sets the AddressInfo of the landmark.
	 *
	 * @param addressInfo
	 *            the AddressInfo of the landmark
	 * @see #getAddressInfo()
	 */
	public void setAddressInfo(AddressInfo addressInfo) {
		this.addressInfo = addressInfo;
	}

	/**
	 * Sets the description of the landmark.
	 *
	 * @param description
	 *            description for the landmark, null may be passed in to indicate that
	 *            description is not available.
	 * @see #getDescription()
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the name of the landmark.
	 *
	 * @param name
	 *            name for the landmark
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @see #getName()
	 */
	public void setName(String name) throws NullPointerException {
		if (name == null)
			throw new NullPointerException();
		this.name = name;
	}

	/**
	 * Sets the QualifiedCoordinates of the landmark.
	 *
	 * @param coordinates
	 *            the qualified coordinates of the landmark
	 * @see #getQualifiedCoordinates()
	 */
	public void setQualifiedCoordinates(QualifiedCoordinates coordinates) {
		this.coordinates = coordinates;
	}
}