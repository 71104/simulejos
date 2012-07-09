package lejos.nxt;

import lejos.util.EndianTools;

/** 
 * Lego Education Temperature Sensor 9749
 * 
 * @author Michael Mirwaldt
 */
public class TemperatureSensor extends I2CSensor
{
	/*
	 * Documentation of the chip can be found here: http://focus.ti.com/docs/prod/folders/print/tmp275.html
	 * Some details from LEGOTMP-driver.h, http://rdpartyrobotcdr.sourceforge.net/
	 */	

	protected static final int I2C_ADDRESS     = 0x98;
	protected static final int REG_TEMPERATURE = 0x00;
	protected static final int REG_CONFIG      = 0x01;
	protected static final int REG_TLOW        = 0x02;
	protected static final int REG_THIGH       = 0x03;
	
	/** 0.5 °C accuracy */
	public static final int RESOLUTION_9BIT = 0;
	/** 0.25 °C accuracy */
	public static final int RESOLUTION_10BIT = 1;
	/** 0.125 °C accuracy */
	public static final int RESOLUTION_11BIT = 2;
	/** 0.0625 C° accuracy */
	public static final int RESOLUTION_12BIT = 3;
	
	/**
	 * Returns, how long it takes the sensor to measure the temperature at the given resolution.
	 * 
	 * @param resolution {@link #RESOLUTION_9BIT}, {@link #RESOLUTION_10BIT}, {@link #RESOLUTION_11BIT}, or {@link #RESOLUTION_12BIT}
	 * @return number of milliseconds
	 */
	public static int getSamplingDelay(int resolution)
	{
		switch (resolution)
		{
			case RESOLUTION_9BIT:
				return 28;
			case RESOLUTION_10BIT:
				return 55;
			case RESOLUTION_11BIT:
				return 110;
			case RESOLUTION_12BIT:
				return 220;
			default:
				throw new IllegalArgumentException();
		}
	}

	private final byte[] buf = new byte[2];
	
	public TemperatureSensor(I2CPort port) {
		super(port, I2C_ADDRESS, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
	}

	public float getTemperature() {
		getData(REG_TEMPERATURE, buf, 2);
		return EndianTools.decodeShortBE(buf, 0)  * 0x1p-8f;
	}

	/**
	 * Returns current resolution.
	 * 
	 * @return {@link #RESOLUTION_9BIT}, {@link #RESOLUTION_10BIT}, {@link #RESOLUTION_11BIT}, or {@link #RESOLUTION_12BIT}
	 */
	public int getResolution() {
		getData(REG_CONFIG, buf, 1);
		return (buf[0] >>> 5) & 0x03;
	}
	
	/**
	 * Sets current resolution.
	 * 
	 * @param resolution {@link #RESOLUTION_9BIT}, {@link #RESOLUTION_10BIT}, {@link #RESOLUTION_11BIT}, or {@link #RESOLUTION_12BIT}
	 */
	public void setResolution(int resolution) {
		if (resolution < 0 || resolution > 3)
			throw new IllegalArgumentException();
		
		//TODO preserve other bits
		sendData(REG_CONFIG, (byte)(resolution << 5));
	}
	
	/**
	 * Sensor does not support Lego standard I2C layout.
	 */
	@Override
	public String getVendorID() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Sensor does not support Lego standard I2C layout.
	 */
	@Override
	public String getProductID() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Sensor does not support Lego standard I2C layout.
	 */
	@Override
	public String getVersion() {
		throw new UnsupportedOperationException();
	}
	
	// TODO sensor can be turned off and on, supports single-shot and continous mode
}
