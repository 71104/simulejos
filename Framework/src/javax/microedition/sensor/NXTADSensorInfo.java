package javax.microedition.sensor;

/**
 * Abstract class with common methods for Lego Analog/Digital sensors.
 * 
 * @author Lawrie Griffiths
 */
public abstract class NXTADSensorInfo extends NXTSensorInfo {
	public int getWiredType() {
		return NXTSensorInfo.AD_SENSOR;
	}
	
	public String getVendor() {
		return "LEGO";
	}
	
	public String getVersion() {
		return "";
	}
}
