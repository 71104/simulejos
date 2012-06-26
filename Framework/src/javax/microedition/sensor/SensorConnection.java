package javax.microedition.sensor;

/**
 * Standard JSR256 SensorConnection interface
 * 
 * @author Lawrie Griffiths
 */
public interface SensorConnection extends javax.microedition.io.Connection {	
	public static final int STATE_CLOSED = 4;
	public static final int STATE_LISTENING = 2; 
	public static final int STATE_OPENED = 1;
	
	public Channel getChannel(ChannelInfo channelInfo);
	
	public Data[] getData(int bufferSize) throws java.io.IOException;
	
	public Data[] getData(int bufferSize,
            long bufferingPeriod,
            boolean isTimestampIncluded,
            boolean isUncertaintyIncluded,
            boolean isValidityIncluded)
     throws java.io.IOException;
	
	public SensorInfo getSensorInfo();
	
	public int getState();
	
	public void removeDataListener();
	
	public void setDataListener(DataListener listener,
            int bufferSize);
	
	public void setDataListener(DataListener listener,
            int bufferSize,
            long bufferingPeriod,
            boolean isTimestampIncluded,
            boolean isUncertaintyIncluded,
            boolean isValidityIncluded);
}
