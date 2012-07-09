package javax.microedition.sensor;

import lejos.nxt.SensorConstants;

/**
 * SensorInfo for LEGO Sound sensor
 * 
 * @author Lawrie Griffiths
 */
public class SoundSensorInfo extends NXTADSensorInfo {

	public SoundSensorInfo() {
		infos = new NXTChannelInfo[]{new SoundChannelInfo()};
	}
	
	public String getQuantity() {
		return "sound_intensity";
	}
	
	public int getSensorType() {
		return SensorConstants.TYPE_SOUND_DB;
	}
	
	public String getType() {
		return "Sound";
	}
}
