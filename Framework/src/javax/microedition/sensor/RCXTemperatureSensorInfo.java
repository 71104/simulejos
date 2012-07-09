package javax.microedition.sensor;

import lejos.nxt.SensorConstants;

/**
 * SensorInfo for the RCX Temperature sensor
 * 
 * @author Lawrie Griffiths
 */
public class RCXTemperatureSensorInfo extends NXTADSensorInfo {

	public RCXTemperatureSensorInfo() {
		infos = new NXTChannelInfo[]{new TemperatureChannelInfo()};
	}
	
	public String getQuantity() {
		return "temperature";
	}
	
	public int getSensorType() {
		return SensorConstants.TYPE_TEMPERATURE;
	}
	
	public String getType() {
		return "RCXTemperature";
	}
}
