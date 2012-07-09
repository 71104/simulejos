package javax.microedition.sensor;

/**
 * Standard JSR256 DataListener interface
 * 
 * @author Lawrie Griffiths
 */
public interface DataListener {
	public void dataReceived(SensorConnection sensor,
            Data[] data,
            boolean isDataLost);
}
