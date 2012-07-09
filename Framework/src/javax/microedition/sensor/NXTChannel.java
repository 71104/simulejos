package javax.microedition.sensor;

/**
 * Implementation of the channel interface for NXT sensor channels
 * 
 * @author Lawrie Griffiths
 */
public class NXTChannel implements Channel {	
	private NXTChannelInfo channelInfo;
	private NXTSensorConnection sensor;
	
	public NXTChannel(NXTSensorConnection sensor, NXTChannelInfo channelInfo) {
		this.sensor = sensor;
		this.channelInfo = channelInfo;
	}
	
	public void addCondition(ConditionListener listener, Condition condition) {
		SensorManager.addCondition(this, listener, condition);
	}

	public NXTChannelInfo getChannelInfo() {
		return channelInfo;
	}

	public String getChannelUrl() {
		// TODO: Add unique conditions
		return channelInfo.getName();
	}

	public Condition[] getConditions(ConditionListener listener) {
		return SensorManager.getConditions(this, listener);
	}

	public void removeAllConditions() {
		SensorManager.removeAllConditions(this);
	}

	public void removeCondition(ConditionListener listener, Condition condition) {
		SensorManager.removeCondition(this, listener, condition);
	}

	public void removeConditionListener(ConditionListener listener) {
		SensorManager.removeConditionListener(this, listener);
	}
	
	public NXTSensorConnection getSensor() {
		return sensor;
	}
}
