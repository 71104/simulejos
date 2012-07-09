package javax.microedition.sensor;

/**
 * Sensor Info for HiTechnic compass
 * 
 * @author Lawrie Griffiths
 */
public class HiTechnicCompassSensorInfo extends NXTSensorInfo {	
	public HiTechnicCompassSensorInfo() {
		infos = new NXTChannelInfo[]{new HeadingChannelInfo()};
	}
	
	public String getQuantity() {
		return "direction";
	}
	
	public String[] getModelNames() {
		return new String[]{"Compass"};
	}
}
