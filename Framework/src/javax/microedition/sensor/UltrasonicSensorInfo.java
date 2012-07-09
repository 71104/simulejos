package javax.microedition.sensor;

/**
 * SensorInfo for the LEGO Mindstorms ultrasonic sensor
 * 
 * @author Lawrie Griffiths
 */
public class UltrasonicSensorInfo extends NXTSensorInfo {
	public UltrasonicSensorInfo() {
		infos = new NXTChannelInfo[]{new UltrasonicChannelInfo()};
	}

	public String getQuantity() {
		return "proximity";
	}
	
	public String[] getModelNames() {
		return new String[]{"Sonar"};
	}
}
