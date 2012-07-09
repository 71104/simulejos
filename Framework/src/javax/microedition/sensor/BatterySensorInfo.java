package javax.microedition.sensor;

public class BatterySensorInfo extends NXTADSensorInfo {

	public BatterySensorInfo() {
		infos = new NXTChannelInfo[]{new BatteryChannelInfo()};
	}
	
	public String getQuantity() {
		return "battery_voltage";
	}
	
	public int getConnectionType() {
		return SensorInfo.CONN_EMBEDDED;
	}
	
	public String getType() {
		return "Battery";
	}
}
