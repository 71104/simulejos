package javax.microedition.sensor;

/**
 * Represents an active condition on a specific channel associated with a 
 * condition listener
 * 
 * @author Lawrie Griffiths
 */
public class NXTActiveCondition {
	private NXTChannel channel;
	private Condition condition;
	private ConditionListener conditionListener;
	
	public NXTActiveCondition(NXTChannel channel, Condition condition, 
			               ConditionListener conditionListener) {
		this.channel = channel;
		this.condition = condition;
		this.conditionListener = conditionListener;
	}
	
	public NXTChannel getChannel() {
		return channel;
	}
	
	public Condition getCondition() {
		return condition;
	}
	
	public ConditionListener getConditionListener() {
		return conditionListener;
	}
}
