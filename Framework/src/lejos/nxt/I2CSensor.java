package lejos.nxt;

import java.lang.IllegalArgumentException;

/**
 * Class that implements common methods for all I2C sensors.
 * 
 * Extend this class to implement new I2C sensors.
 * 
 * @author Lawrie Griffiths (lawrie.griffiths@ntlworld.com).
 * 
 */
public class I2CSensor implements SensorConstants {
	/**
	 * Register number of sensor version string, as defined by standard Lego I2C
	 * register layout.
	 * 
	 * @see #getVersion()
	 */
	protected static final byte REG_VERSION = 0x00;
	/**
	 * Register number of sensor vendor ID, as defined by standard Lego I2C
	 * register layout.
	 * 
	 * @see #getVendorID()
	 */
	protected static final byte REG_VENDOR_ID = 0x08;
	/**
	 * Register number of sensor product ID, as defined by standard Lego I2C
	 * register layout.
	 * 
	 * @see #getProductID()
	 */
	protected static final byte REG_PRODUCT_ID = 0x10;

	protected static final int DEFAULT_I2C_ADDRESS = 0x02;

	protected I2CPort port;
	protected int address;
	private byte[] ioBuf = new byte[32];

	public I2CSensor(I2CPort port) {
		this(port, DEFAULT_I2C_ADDRESS, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
	}

	public I2CSensor(I2CPort port, int mode) {
		this(port, DEFAULT_I2C_ADDRESS, mode, TYPE_LOWSPEED);
	}

	public I2CSensor(I2CPort port, int address, int mode, int type) {
		this.port = port;
		this.address = address;
		port.setType(type);
		port.i2cEnable(mode);
	}

	/**
	 * Executes an I2C read transaction and waits for the result.
	 * 
	 * @param register
	 *            I2C register, e.g 0x41
	 * @param buf
	 *            Buffer to return data
	 * @param len
	 *            Length of the return data
	 * @return negative value on error, 0 otherwise
	 */
	public int getData(int register, byte[] buf, int len) {
		return getData(register, buf, 0, len);
	}

	/**
	 * Executes an I2C read transaction and waits for the result.
	 * 
	 * @param register
	 *            I2C register, e.g 0x41
	 * @param buf
	 *            Buffer to return data
	 * @param offset
	 *            Offset of the start of the data
	 * @param len
	 *            Length of the return data
	 * @return negative value on error, 0 otherwise
	 */
	public synchronized int getData(int register, byte[] buf, int offset,
			int len) {
		// need to write the internal address.
		ioBuf[0] = (byte) register;
		int ret = port.i2cTransaction(address, ioBuf, 0, 1, buf, offset, len);
		return (ret < 0 ? ret : (ret == len ? 0 : -1));
	}

	/**
	 * Executes an I2C write transaction.
	 * 
	 * @param register
	 *            I2C register, e.g 0x42
	 * @param buf
	 *            Buffer containing data to send
	 * @param len
	 *            Length of data to send
	 * @return negative value on error, 0 otherwise
	 */
	public int sendData(int register, byte[] buf, int len) {
		return sendData(register, buf, 0, len);
	}

	/**
	 * Executes an I2C write transaction.
	 * 
	 * @param register
	 *            I2C register, e.g 0x42
	 * @param buf
	 *            Buffer containing data to send
	 * @param offset
	 *            Offset of the start of the data
	 * @param len
	 *            Length of data to send
	 * @return negative value on error, 0 otherwise
	 */
	public synchronized int sendData(int register, byte[] buf, int offset,
			int len) {
		if (len >= ioBuf.length)
			throw new IllegalArgumentException();
		ioBuf[0] = (byte) register;
		// avoid NPE in case length==0 and data==null
		if (len > 0)
			System.arraycopy(buf, offset, ioBuf, 1, len);
		return port.i2cTransaction(address, ioBuf, 0, len + 1, null, 0, 0);
	}

	/**
	 * Executes an I2C write transaction.
	 * 
	 * @param register
	 *            I2C register, e.g 0x42
	 * @param value
	 *            single byte to send
	 * @return negative value on error, 0 otherwise
	 */
	public synchronized int sendData(int register, byte value) {
		ioBuf[0] = (byte) register;
		ioBuf[1] = value;
		return port.i2cTransaction(address, ioBuf, 0, 2, null, 0, 0);
	}

	/**
	 * Read the sensor's version string. This method reads up to 8 bytes and
	 * returns the characters before the zero termination byte. Examples:
	 * "V1.0", ...
	 * 
	 * @return version number
	 */
	public String getVersion() {
		return fetchString(REG_VERSION, 8);
	}

	/**
	 * Read the sensor's vendor identifier. This method reads up to 8 bytes and
	 * returns the characters before the zero termination byte. Examples:
	 * "LEGO", "HiTechnc", ...
	 * 
	 * @return vendor identifier
	 */
	public String getVendorID() {
		return fetchString(REG_VENDOR_ID, 8);
	}

	/**
	 * Read the sensor's product identifier. This method reads up to 8 bytes and
	 * returns the characters before the zero termination byte. Examples:
	 * "Sonar", ...
	 * 
	 * @return product identifier
	 */
	public String getProductID() {
		return fetchString(REG_PRODUCT_ID, 8);
	}

	/**
	 * Read a string from the device. This functions reads the specified number
	 * of bytes and returns the characters before the zero termination byte.
	 * 
	 * @param reg
	 * @param len
	 *            maximum length of the string, including the zero termination
	 *            byte
	 * @return the string containing the characters before the zero termination
	 *         byte
	 */
	protected String fetchString(byte reg, int len) {
		byte[] buf = new byte[len];
		int ret = getData(reg, buf, 0, len);
		if (ret != 0)
			return "";

		int i;
		char[] charBuff = new char[len];
		for (i = 0; i < len && buf[i] != 0; i++)
			charBuff[i] = (char) (buf[i] & 0xFF);

		return new String(charBuff, 0, i);
	}

	/**
	 * Set the address of the port Addresses use the standard Lego/NXT format
	 * and are in the range 0x2-0xfe. The low bit must always be zero. Some data
	 * sheets (and older versions of leJOS) may use i2c 7 bit format (0x1-0x7f)
	 * in which case this address must be shifted left one place to be used with
	 * this function.
	 * 
	 * @param addr
	 *            0x02 to 0xfe
	 * @deprecated If the device has a changeable address, then constructor of
	 *             the class should have an address parameter. If not, please
	 *             report a bug.
	 */
	@Deprecated
	public void setAddress(int addr) {
		if ((address & 1) != 0)
			throw new IllegalArgumentException("Bad address format");
		address = addr;
	}

	/**
	 * Return the the I2C address of the sensor. The sensor uses the address for
	 * writing/reading.
	 * 
	 * @return the I2C address.
	 */
	public int getAddress() {
		return this.address;
	}

	/**
	 * Get the port that the sensor is attached to
	 * 
	 * @return the I2CPort
	 */
	public I2CPort getPort() {
		return port;
	}
}
