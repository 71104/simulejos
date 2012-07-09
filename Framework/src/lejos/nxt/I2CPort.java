package lejos.nxt;

/**
 * Abstraction for a port that supports I2C sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public interface I2CPort extends BasicSensorPort {
    /** Use standard i2c protocol */
    public static final int STANDARD_MODE = 0;
    /** Use Lego compatible i2c protocol (default) */
    public static final int LEGO_MODE = 1;
    /** Keep the i2c driver active between requests */
    public static final int ALWAYS_ACTIVE = 2;
    /** Do not release the i2c bus between requests */
    public static final int NO_RELEASE = 4;
    /** Use high speed I/O (125KHz) */
    public static final int HIGH_SPEED = 8;
    /** Maximum read/write request length */
    public static final int MAX_IO = 32;

    /** Invalid port number, or port is not enabled */
    public static final int ERR_INVALID_PORT = -1;
    /** Port is busy */
    public static final int ERR_BUSY = -2;
    /** Data error during transaction */
    public static final int ERR_FAULT = -3;
    /** Read/Write request too large */
    public static final int ERR_INVALID_LENGTH = -4;
    /** Bus is busy */
    public static final int ERR_BUS_BUSY = -5;
    /** Operation aborted */
    public static final int ERR_ABORT = -6;



    /**
     * Enable the low level device
     * @param mode One or more of the mode bits above.
     */
	public void i2cEnable(int mode);

    /**
     * Disable the device.
     */
	public void i2cDisable();

    /**
     * Check to see the status of the port/device
     * @return 0 if ready
     *         -1: Invalid device
     *         -2: Device busy
     *         -3: Device fault
     *         -4: Buffer size error.
     *         -5: Bus is busy
     */
	public int i2cStatus();

    /**
     * High level i2c interface. Perform a complete i2c transaction and return
     * the results. Writes the specified data to the device and then reads the
     * requested bytes from it. The address is given as an 8 bit value. Bit 0
     * must be always be zero. Bit 1 to 7 specify the 7 bit i2c address.
     * 
     * @param deviceAddress The I2C device address.
     * @param writeBuf The buffer containing data to be written to the device.
     * @param writeOffset The offset of the data within the write buffer
     * @param writeLen The number of bytes to write.
     * @param readBuf The buffer to use for the transaction results
     * @param readOffset Location to write the results to
     * @param readLen The length of the read
     * @return < 0 error otherwise the number of bytes read
     */
    public int i2cTransaction(int deviceAddress, byte[]writeBuf,
            int writeOffset, int writeLen, byte[] readBuf, int readOffset,
            int readLen);
}
