package javax.microedition.sensor;

import lejos.nxt.SensorConstants;

/**
 * Abstract class that provides default methods for leJOS NXT sensors
 * 
 * @author Lawrie Griffiths
 */
public abstract class NXTSensorInfo implements SensorInfo {
	private String vendor, version, type;
	private int portNumber = -1;
	protected NXTChannelInfo[] infos;
	// Default reading rate, once every 20 milliseconds
	protected static final int MAX_RATE = 50;
	
	public static final int I2C_SENSOR = 0;
	public static final int AD_SENSOR = 1;
	public static final int RCX_SENSOR = 2;

	public int getConnectionType() {
		return SensorInfo.CONN_WIRED;
	}

	public String getContextType() {
		return SensorInfo.CONTEXT_TYPE_DEVICE;
	}

	public int getMaxBufferSize() {
		return 256;
	}

	public boolean isAvailabilityPushSupported() {
		return (getWiredType() == NXTSensorInfo.I2C_SENSOR);
	}

	public boolean isAvailable() {
		if (getWiredType() == NXTSensorInfo.I2C_SENSOR) {
			return SensorManager.findSensors(getUrl()) != null;
		} else return true;
	}

	public boolean isConditionPushSupported() {
		return true;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String getVendor() {
		return vendor;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getModel() {
		return getVendor() + "." + getType() + "." +getVersion();
	}
	
	public Object getProperty(String name) {
		if (name.equals(SensorInfo.PROP_VENDOR)) return getVendor();;
		if (name.equals(SensorInfo.PROP_VERSION)) return getVersion();
		if (name.equals(SensorInfo.PROP_MAX_RATE)) return MAX_RATE;
		return null;
	}
	
	public String[] getPropertyNames() {
		return new String[]{SensorInfo.PROP_VENDOR, SensorInfo.PROP_VERSION, SensorInfo.PROP_MAX_RATE};
	}
	
	public String getDescription() {
		return getVendor() + " " + getModel() + " " + getVersion();
	}
	
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public String getUrl() {
		// Port is a leJOS NXJ extension
		return "sensor:" + getQuantity() + ";contextType=" + getContextType() +
		       ";model=" + getModel() + ";port=" + (portNumber+1);
	}
		
	public NXTChannelInfo[] getChannelInfos() {
		return infos;
	}
	
	public int getWiredType() {
		return I2C_SENSOR;
	}
	
	public int getMode() {
		return SensorConstants.MODE_PCTFULLSCALE;
	}
	
	public int getSensorType() {
		return SensorConstants.TYPE_LOWSPEED;
	}
	
	/**
	 * Return the names of all the models that implement this channel.
	 * Null for A/D sensors.
	 * 
	 * @return the model names
	 */
	public String[] getModelNames() {
		return null;
	}
}
