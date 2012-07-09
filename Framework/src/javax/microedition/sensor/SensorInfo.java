package javax.microedition.sensor;

/**
 * Standard JSR256 SensorInfo interface
 * 
 * @author Lawrie Griffiths
 */
public interface SensorInfo {
	public static final int CONN_EMBEDDED = 1; 
	public static final int CONN_REMOTE = 2; 
	public static final int CONN_SHORT_RANGE_WIRELESS = 4; 
	public static final int CONN_WIRED = 8; 
	public static final String CONTEXT_TYPE_AMBIENT = "ambient"; 
	public static final String CONTEXT_TYPE_DEVICE = "device"; 
	public static final String CONTEXT_TYPE_USER = "user"; 
	public static final String PROP_LATITUDE = "latitude"; 
	public static final String PROP_LOCATION = "location"; 
	public static final String PROP_LONGITUDE = "longitude"; 
	public static final String PROP_MAX_RATE = "maxSamplingRate"; 
	public static final String PROP_VENDOR = "vendor" ;
	public static final String PROP_VERSION = "version";
	
	public ChannelInfo[] getChannelInfos();
	
	public int getConnectionType();
	
	public String getContextType();
	
	public String getDescription();
	
	public int getMaxBufferSize();
	
	public String getModel();
	
	public Object getProperty(String name);
	
	public String[] getPropertyNames();
	
	public String getQuantity();
	
	public String getUrl();
	
	public boolean isAvailabilityPushSupported();
	
	public boolean isAvailable();
	
	public boolean isConditionPushSupported();
}
