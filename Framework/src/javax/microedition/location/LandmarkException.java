package javax.microedition.location;

/**
 * The LandmarkException is thrown when an error related to handling landmarks has
 * occurred.
 */
public class LandmarkException extends Exception {

	/**
	 * Constructs a LandmarkException with no detail message.
	 */
	public LandmarkException() {
	}

	/**
	 * Constructs a LandmarkException with the specified detail message.
	 *
	 * @param s
	 *            the detailed message
	 */
	public LandmarkException(String s) {
		super(s);
	}
}
