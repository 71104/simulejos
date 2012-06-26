package javax.microedition.sensor;

import lejos.nxt.SensorConstants;

/**
 * SensorInfo for LEGO Touch sensor
 * 
 * @author Lawrie Griffiths
 */
public class TouchSensorInfo extends NXTADSensorInfo {

	public TouchSensorInfo() {
		infos = new NXTChannelInfo[]{new TouchChannelInfo()};
	}
	
	public String getQuantity() {
		return "touch";
	}
	
	public int getSensorType() {
		return SensorConstants.TYPE_SWITCH;
	}
	
	public String getType() {
		return "Touch";
	}
}
