package javax.microedition.sensor;

/**
 * Sensor Info for HiTechnic Color sensor
 * 
 * @author Lawrie Griffiths
 */
public class HiTechnicColorSensorInfo extends NXTSensorInfo {
	public HiTechnicColorSensorInfo() {
		infos = new NXTChannelInfo[]{new ColorChannelInfo(), 
			                         new ColorIndexChannelInfo(),
			                         new ColorRGBChannelInfo("r"),
			                         new ColorRGBChannelInfo("g"),
			                         new ColorRGBChannelInfo("b")};
	}
	
	public String getQuantity() {
		return "org.lejos.color";
	}
	
	public String[] getModelNames() {
		return new String[]{"Color"};
	}
}
