package lejos.nxt;

/**
 * 
 * Interface for monitoring changes to the value for an 
 * Analogue/Digital sensor (such as a Touch, Light or Sound sensor)
 * on a SensorPort.
 * 
 */
public interface SensorPortListener 
{
	
	  /**
	   * Called when the raw value of the sensor attached to the port changes.
	   * @param aSource The Port that generated the event.
	   * @param aOldValue The old sensor raw value.
	   * @param aNewValue The new sensor raw value.
	   */
	  public void stateChanged (SensorPort aSource, int aOldValue, int aNewValue);
}

