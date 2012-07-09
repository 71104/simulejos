package javax.microedition.sensor;

/** 
 * Implementation of the Data interface for NXT Sensors. 
 * 
 * @author Lawrie Griffiths
 */
public class NXTData implements Data {
	protected int[] values;
	protected long timeStamp = System.currentTimeMillis();
	protected ChannelInfo info;
	
	public NXTData(ChannelInfo info, int bufferSize) {
		this.info = info;
		values = new int[bufferSize];
	}
	
	void setIntData(int index, int value) {
		values[index] = value;
	}
	
	public ChannelInfo getChannelInfo() {
		return info;
	}

	public double[] getDoubleValues() {
		throw new IllegalStateException();
	}

	public int[] getIntValues() {
		return values;
	}

	public Object[] getObjectValues() {
		throw new IllegalStateException();
	}

	public long getTimestamp(int index) {
		return timeStamp;
	}

	public float getUncertainty(int index) {
		return 0;
	}

	public boolean isValid(int index) {
		return true;
	}
}
