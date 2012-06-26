package javax.microedition.sensor;

/**
 * Represents active asynchronous transfers
 * 
 * @author Lawrie
 *
 */
public class NXTActiveData {
	private NXTSensorConnection sensor;
	private int bufferSize;
	private DataListener listener;
	private int samplingInterval;
	private NXTData[] data;
	private int position = 0;
	private long lastSampleMillis=0;
	
	public NXTActiveData(NXTSensorConnection sensor, int bufferSize, DataListener listener, int samplingInterval) {
		this.sensor = sensor;
		this.bufferSize =bufferSize;
		this.listener = listener;
		this.samplingInterval = samplingInterval;
		data = createData();
	}
	
	public SensorConnection getSensor() {
		return sensor;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	public DataListener getListener() {
		return listener;
	}
	
	public int getSamplingInterval() {
		return samplingInterval;
	}
	
	/**
	 * Process the entry. Check if we are ready to read a new sample and if so, read it.
	 * When the buffer is full, call the data listener and start a new buffer
	 */
	public void process() {
		if ((System.currentTimeMillis() - lastSampleMillis) >= samplingInterval) {
			//Read all the channels
			NXTChannelInfo[] channelInfos = (NXTChannelInfo[]) sensor.getSensorInfo().getChannelInfos();				
			for(int i=0;i<channelInfos.length;i++) {
				data[i].setIntData(position, sensor.getChannelData(channelInfos[i]));
			}			
			if (++position == bufferSize) {
				listener.dataReceived(sensor, data, false);
				data = createData();
				position = 0;
			}
			lastSampleMillis = System.currentTimeMillis();
		}	
	}
	
	private NXTData[] createData() {
		NXTChannelInfo[] channelInfos = (NXTChannelInfo[]) sensor.getSensorInfo().getChannelInfos();
		NXTData[] data = new NXTData[channelInfos.length];
		for(int i=0;i<channelInfos.length;i++) {
			data[i] = new NXTData(channelInfos[i], bufferSize);
		}
		return data;
	}
}
