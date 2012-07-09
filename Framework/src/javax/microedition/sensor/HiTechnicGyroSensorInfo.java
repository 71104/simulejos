package javax.microedition.sensor;

import lejos.nxt.SensorConstants;

/**
 * Sensor Info for HiTechnic compass
 * 
 * @author Lawrie Griffiths
 */
public class HiTechnicGyroSensorInfo extends NXTADSensorInfo {	
	public HiTechnicGyroSensorInfo() {
		infos = new NXTChannelInfo[]{new GyroChannelInfo()};
	}
	
	public String getQuantity() {
		return "angular_velocity";
	}
	
	
	public int getSensorType() {
		return SensorConstants.TYPE_CUSTOM;
	}
	
	public String getType() {
		return "Gyro";
	}
}
