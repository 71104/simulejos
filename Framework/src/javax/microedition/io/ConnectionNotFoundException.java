package javax.microedition.io;

import java.io.IOException;

/**
 * This class is used to signal that a connection target cannot be found, or the protocol type is not supported. 
 * @author BB
 *
 */
public class ConnectionNotFoundException extends IOException {

	/**
	 * Constructs a ConnectionNotFoundException with no detail message.
	 */
	public ConnectionNotFoundException() {
		super();
	}
    
	/**
	 * Constructs a ConnectionNotFoundException with the specified detail message.
	 * @param s
	 */
	public ConnectionNotFoundException(String s) {
		super(s);
	}
}
