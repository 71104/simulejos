package javax.microedition.sensor;

/**
 * Standard JSR256 SensorListener interface
 * 
 * @author Lawrie Griffiths
 */
public interface SensorListener {
	public void sensorAvailable(SensorInfo info);
	
	public void sensorUnavailable(SensorInfo info);
}
