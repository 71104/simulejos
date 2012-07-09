package javax.microedition.sensor;

import lejos.nxt.SensorConstants;

/**
 * SensorInfo for LEGO light sensor
 * 
 * @author Lawrie Griffiths
 */
public class RCXLightSensorInfo extends NXTADSensorInfo {

	public RCXLightSensorInfo() {
		infos = new NXTChannelInfo[]{new LightChannelInfo()};
	}
	
	public String getQuantity() {
		return "luminance";
	}
	
	public int getSensorType() {
		return SensorConstants.TYPE_REFLECTION;
	}
	
	public String getType() {
		return "RCXLight";
	}
}
