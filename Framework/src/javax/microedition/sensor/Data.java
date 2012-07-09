package javax.microedition.sensor;

/**
 * Standard JSR256 data interface
 * 
 * @author Lawrie Griffiths
 */
public interface Data {

	public ChannelInfo getChannelInfo();
	
	public double[] getDoubleValues();
	
	public int[] getIntValues();
	
	public Object[] getObjectValues();
	
	public long getTimestamp(int index);
	
	public float getUncertainty(int index);
	
	public boolean isValid(int index);
}
