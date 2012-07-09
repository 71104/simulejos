package javax.microedition.io;

import java.io.*;
import javax.microedition.sensor.NXTSensorConnection;

import lejos.nxt.comm.*;

/**
 * This is a factory class to create different data connections, such as Bluetooth or USB.
 * Currently it only supports Bluetooth SPP (btspp://). 
 * @author BB
 *
 */
public class Connector {
	
	/**
     * Access mode READ. The value 1 is assigned to READ.
     */
    public static final int READ = 1;

    /**
     * Access mode WRITE. The value 2 is assigned to WRITE.
     */
    public static final int WRITE = 2;

    /**
     * Access mode READ_WRITE. The value 3 is assigned to READ_WRITE.
     */
    public static final int READ_WRITE = 3;

    private Connector() {}

    /**
     * Create and open a Connection.
     * 
     * @param name The URL for the connection.
     * @return A new Connection object.
     * @throws IllegalArgumentException - If a parameter is invalid.
     * @throws ConnectionNotFoundException - If the requested connection cannot be made,
     * or the protocol type does not exist. 
     * @throws java.io.IOException - If some other kind of I/O error occurs. 
     */
    public static Connection open(String name) throws IOException {
        Connection c = null;
    	// TODO Sometimes the address will include the channel number, as in:
        // "btspp://001BC1016D10:3" If we add support for channel in Bluetooth might want
        // to pass this along too. If not, take care of it here.
        
    	// 1. Parse out scheme, target, and possibly parameters from string.
    	String scheme = parseScheme(name);
    	final String target = parseTarget(name);
    	// 2. If scheme = "btspp" then continue else throw ConnectionNotFound exception
    	if(scheme.equals("btspp")) {
    		// 3. Use Bluetooth.connect to get a BTConnection and return it.
        	c = new StreamConnectionNotifier() {
        		
        		private StreamConnection sc = null;
        		
				public StreamConnection acceptAndOpen() throws IOException {
					return Bluetooth.connect(target, NXTConnection.RAW);
				}

				public void close() throws IOException {
					if(sc != null) sc.close();
				}
        	};
    	} else if (scheme.equals("sensor")) {
    		c =  new NXTSensorConnection(name);
    	} else {
    		throw new ConnectionNotFoundException(scheme + " not a known protocol");
    	}
    	
    	return c;
    }

    private static String parseScheme(String name) {
    	int end = name.indexOf(':');
    	return name.substring(0, end);
    }
    
    /**
     * This method parses out only the target. If the target has a colon for a port
     * or channel, it will include this as the target! e.g. "btspp://001BC1016D10:3;PIN=1234"
     * will return "001BC1016D10:3"
     * TODO: Perhaps this method should ignore a port for now? Will crash if user includes port.
     * @param name
     * @return
     */
    private static String parseTarget(String name) {
    	int start = name.indexOf("://") + 3;
    	int end = name.indexOf(';', start);
    	if(end == -1) end = name.length();
    	if (start < 0) start = 0;
    	return name.substring(start, end);
    }
        
    /**
     * Create and open a Connection.
     * 
     * @param name The URL for the connection.
     * @param mode The mode for connection (READ, WRITE, READ_WRITE)
     * @return A new Connection object.
     * @throws IllegalArgumentException - If a parameter is invalid.
     * @throws ConnectionNotFoundException - If the requested connection cannot be made,
     * or the protocol type does not exist. 
     * @throws java.io.IOException - If some other kind of I/O error occurs. 
     */

    public static Connection open(String name, int mode) throws IOException {
    	// TODO Implement this if mode applies to any of our streams (USB, RS485, etc)
    	return open(name);
    }

    /**
     * Create and open a Connection.
     * 
     * @param name The URL for the connection.
     * @param mode The mode for connection (READ, WRITE, READ_WRITE)
     * @param timeouts 
     * @return A new Connection object.
     * @throws IllegalArgumentException - If a parameter is invalid.
     * @throws ConnectionNotFoundException - If the requested connection cannot be made,
     * or the protocol type does not exist. 
     * @throws java.io.IOException - If some other kind of I/O error occurs. 
     */
    public static Connection open(String name, int mode, boolean timeouts) throws IOException {
    	// TODO Implement this if mode or timeouts applies to any of our streams (USB, RS485, etc)
    	return open(name);
    }

    /**
     * Create and open a connection input stream. 
     *
     * @param name The URL for the connection stream.
     * @return the data input stream
     * @throws IOException
     */
    public static DataInputStream openDataInputStream(String name) throws IOException {
    	InputConnection ic = (InputConnection)open(name);
        return ic.openDataInputStream();
    }

    /**
     * Create and open a connection output stream.
     * 
     * @param name The URL for the connection.
     * @return the data output stream
     * @throws IOException
     */
    public static DataOutputStream openDataOutputStream(String name) throws IOException {
    	OutputConnection oc = (OutputConnection)open(name);
        return oc.openDataOutputStream();
    }

    /**
     * Create and open a connection input stream.
     * @param name The URL for the connection.
     * @return the input stream
     * @throws IOException
     */
    public static InputStream openInputStream(String name) throws IOException {
    	InputConnection ic = (InputConnection)open(name);
        return ic.openInputStream();
    }

    /**
     * Create and open a connection output stream.
     * @param name The URL for the connection.
     * @return the output stream
     * @throws IOException
     */

    public static OutputStream openOutputStream(String name) throws IOException {
    	OutputConnection oc = (OutputConnection)open(name);
        return oc.openOutputStream();
    }
}
