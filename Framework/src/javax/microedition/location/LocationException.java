package javax.microedition.location;

/**
 * The LocationException is thrown when a location API specific error has occurred. The
 * detailed conditions when this exception is thrown are documented in the methods that
 * throw this exception.
 */

public class LocationException extends Exception {

	/**
	 * Constructs a LocationException with no detail message.
	 */
	public LocationException() {
	}

	/**
	 * Constructs a LocationException with the specified detail message.
	 *
	 * @param s the detailed message
	 */
	public LocationException(String s) {
		super(s);
	}
}
