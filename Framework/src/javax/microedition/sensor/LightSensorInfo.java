package javax.microedition.sensor;

import lejos.nxt.SensorConstants;

/**
 * SensorInfo for LEGO light sensor
 * 
 * @author Lawrie Griffiths
 */
public class LightSensorInfo extends NXTADSensorInfo {

	public LightSensorInfo() {
		infos = new NXTChannelInfo[]{new LightChannelInfo()};
	}
	
	public String getQuantity() {
		return "luminance";
	}
	
	public int getSensorType() {
		return SensorConstants.TYPE_LIGHT_ACTIVE;
	}
	
	public String getType() {
		return "Light";
	}
}
