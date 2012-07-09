package javax.microedition.sensor;

/**
 * Standard JSR256 ConditionListener interface
 * 
 * @author Lawrie Griffiths
 */
public interface ConditionListener {
	
	public void conditionMet(SensorConnection sensor,
            Data data,
            Condition condition);
}
