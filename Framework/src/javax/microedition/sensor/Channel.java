package javax.microedition.sensor;

/**
 * Standard JSR256 Channel interface
 * 
 * @author Lawrie Griffiths
 */
public interface Channel {
	public void addCondition(ConditionListener listener, Condition condition);
	
	public ChannelInfo getChannelInfo();
	
	public String getChannelUrl();
	
	public Condition[] getConditions(ConditionListener listener);
	
	public void removeAllConditions();
	
	public void removeCondition(ConditionListener listener, Condition condition);
	
	public void removeConditionListener(ConditionListener listener);
}
