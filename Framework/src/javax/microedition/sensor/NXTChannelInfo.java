package javax.microedition.sensor;

/**
 * Abstract implementation ChannelInfo with extra methods needed for NXT sensors
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class NXTChannelInfo implements ChannelInfo {	
	/**
	 * Get the register that returns channel data for an I2C channel
	 * @return the register
	 */
	public int getRegister() {
		return 0x42;
	}

	public float getAccuracy() {
		return 0;
	}

	public int getDataType() {
		return ChannelInfo.TYPE_INT;
	}

	public int getScale() {
		return 0;
	}

	public int getDataLength() {
		return 8;
	}
	
	public Unit getUnit() {
		return Unit.getUnit("");
	}
	
	/**
	 * Get the value that represents zero.
	 * 
	 * @return the offset
	 */
	public int getOffset() {
		return 0;
	}
}
