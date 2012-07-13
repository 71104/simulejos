package lejos.nxt;

import it.uniroma1.di.simulejos.NotImplementedException;
import it.uniroma1.di.simulejos.bridge.Bridge;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import lejos.util.Delay;

/**
 * Abstraction for a NXT input port.
 * 
 */
public class SensorPort implements LegacySensorPort, I2CPort, ListenerCaller {
	/** Power types. 5V standard. */
	public static final int POWER_STD = 0;
	/** Power types. 9V pulsed as per RCX. */
	public static final int POWER_RCX9V = 1;
	/** Power types. 9V */
	public static final int POWER_9V = 2;
	/** Sensor port digital I/O 0 (pin 5 on connector) */
	public static final int SP_DIGI0 = 0;
	/** Sensor port digital I/O 1 (pin 6 on connector) */
	public static final int SP_DIGI1 = 1;
	/** Sensor port analogue input (pin 1 on connector) */
	public static final int SP_ANA = 2;
	/** Sensor port pin mode. Pin is disabled */
	public static final int SP_MODE_OFF = 0;
	/** Sensor port pin mode. Pin is digital input */
	public static final int SP_MODE_INPUT = 1;
	/** Sensor port pin mode. Pin is digital output */
	public static final int SP_MODE_OUTPUT = 2;
	/** Sensor port pin mode. Pin is analogue input */
	public static final int SP_MODE_ADC = 3;

	/**
	 * The number of ports available.
	 */
	public static final int NUMBER_OF_PORTS = 4;

	/**
	 * Port labeled 1 on NXT.
	 */
	public static final SensorPort S1 = new SensorPort(0, Bridge.getSimulator()
			.getS1());
	/**
	 * Port labeled 2 on NXT.
	 */
	public static final SensorPort S2 = new SensorPort(1, Bridge.getSimulator()
			.getS2());
	/**
	 * Port labeled 3 on NXT.
	 */
	public static final SensorPort S3 = new SensorPort(2, Bridge.getSimulator()
			.getS3());
	/**
	 * Port labeled 4 on NXT.
	 */
	public static final SensorPort S4 = new SensorPort(3, Bridge.getSimulator()
			.getS4());

	/**
	 * Array containing all three ports [0..3].
	 * 
	 * @deprecated use {@link #getInstance(int)} instead.
	 */
	@Deprecated
	public static final SensorPort[] PORTS = { SensorPort.S1, SensorPort.S2,
			SensorPort.S3, SensorPort.S4 };

	private final SimulatorInterface.Sensor sensor;

	/** I2C event filter bits */
	private static final int I2C_IO_COMPLETE = 1;
	private static final int I2C_BUS_FREE = 0x100;
	private static final int I2C_BUS_FREE_TIMEOUT = 10;

	/**
	 * Return the SensorPort with the given Id.
	 * 
	 * @param id
	 *            the Id, between 0 and {@link #NUMBER_OF_PORTS}-1.
	 * @return the SensorPort object
	 */
	public static SensorPort getInstance(int id) {
		switch (id) {
		case 0:
			return SensorPort.S1;
		case 1:
			return SensorPort.S2;
		case 2:
			return SensorPort.S3;
		case 3:
			return SensorPort.S4;
		default:
			throw new IllegalArgumentException("no such sensor port");
		}
	}

	private int iPortId;
	private short iNumListeners = 0;
	private SensorPortListener[] iListeners;
	private int iPreviousValue;
	private int type, mode;
	private NXTEvent i2cEvent;
	private int i2cSensorCnt = 0;
	private boolean i2cHighSpeed = false;
	/**
	 * The event filter for a sensor port allows for less than and greater then
	 * a target value (with a +/- tolerance).
	 */
	private int listenerFilter = 0;
	private static final int GT_EVENT_SHIFT = 0;
	private static final int LT_EVENT_SHIFT = 4;
	private static final int TARGET_SHIFT = 8;
	private static final int TARGET_MASK = 0x00003ff00;
	private static final int TOLERANCE_SHIFT = 18;
	private static final int TOLERANCE_MASK = 0x03fc0000;
	private static final int DEFAULT_TOLERANCE = 2;

	/**
	 * The SensorReader class provides a type dependent way to obtain data from
	 * a sensor. This base class simply returns no data.
	 */
	protected class SensorReader {
		/**
		 * Used to notify the reader that the type of the sensor has changed.
		 * 
		 * @param type
		 */
		public void setType(int type) {
		}

		/**
		 * Used to notify the reader that the operating mode of the sensor has
		 * changed.
		 * 
		 * @param mode
		 */
		public void setMode(int mode) {
		}

		/**
		 * Read a normalised/calibrated value from the sensor.
		 * 
		 * @return < 0 error, >= 0 sensor value
		 */
		public int readValue() {
			return -1;
		}

		/**
		 * Read a raw value from the sensor.
		 * 
		 * @return < 0 error >= 0 Raw sensor value.
		 */
		public int readRawValue() {
			return -1;
		}

		/**
		 * Return a variable number of sensor values
		 * 
		 * @param values
		 *            An array in which to return the sensor values.
		 * @return The number of values returned.
		 */
		public int readValues(int[] values) {
			return -1;
		}

		/**
		 * Return a variable number of raw sensor values
		 * 
		 * @param values
		 *            An array in which to return the sensor values.
		 * @return The number of values returned.
		 */
		public int readRawValues(int[] values) {
			return -1;
		}

		/**
		 * Reset the sensor.
		 */
		public void reset() {
		}

	}

	protected class StandardReader extends SensorReader {
		/**
		 * Returns value compatible with Lego firmware.
		 * 
		 * @return the computed value
		 */
		@Override
		public int readValue() {
			int rawValue = readSensorValue(iPortId);

			if (mode == MODE_BOOLEAN)
				return (rawValue < 600 ? 1 : 0);

			if (mode == MODE_PCTFULLSCALE)
				return ((1023 - rawValue) * 100 / 1023);

			return rawValue;
		}

		/**
		 * Reads the raw value of the sensor.
		 * 
		 * @return the raw sensor value
		 */
		@Override
		public final int readRawValue() {
			return readSensorValue(iPortId);
		}
	}

	/**
	 * Lego Color Sensor driver. This driver provides access to the Lego Color
	 * sensor. It allows the reading raw and processed color values. The sensor
	 * has a tri-color led and this can be set to output red/green/blue or off.
	 * It also has a full mode in which four samples are read
	 * (off/red/green/blue) very quickly. These samples can then be combined
	 * using the calibration data provided by the device to determine the "Lego"
	 * color currently being viewed.
	 * 
	 * @author andy
	 */
	protected class ColorSensorReader extends SensorReader {
		/**
		 * Sensor types supported by this driver. The type is used to control
		 * the operation of the tri color led.
		 */
		// pin usage for clock and data lines.
		private static final int CLOCK = SensorPort.SP_DIGI0;
		private static final int DATA = SensorPort.SP_DIGI1;
		private boolean initialized = false;
		private int type = TYPE_NO_SENSOR;
		// data ranges and limits
		private static final int ADVOLTS = 3300;
		private static final int ADMAX = 1023;
		private static final int MINBLANKVAL = (214 / (ADVOLTS / ADMAX));
		private static final int SENSORMAX = ADMAX;
		private int[][] calData = new int[3][4];
		private int[] calLimits = new int[2];
		private int[] rawValues = new int[BLANK_INDEX + 1];
		private int[] values = new int[BLANK_INDEX + 1];

		/**
		 * Create a new Color Sensor instance and bind it to a port.
		 */
		public ColorSensorReader() {
			initialized = false;
		}

		/**
		 * Change the type of the sensor
		 * 
		 * @param type
		 *            new sensor type.
		 */
		@Override
		public void setType(int type) {
			if (type != TYPE_NO_SENSOR) {
				if (this.type != type) {
					this.type = type;
					initialized = false;
					checkInitialized();
				}
			} else
				reset();
		}

		/**
		 * Reset the sensor.
		 */
		@Override
		public void reset() {
			// It would seem that the only way to reset the sensor is to either
			// power it off, or set it to the color none type.
			setType(TYPE_COLORNONE);
			type = TYPE_NO_SENSOR;
		}

		/**
		 * Set the clock pin to the specified value
		 * 
		 * @param val
		 *            the new value(0/1) for the pin.
		 */
		private void setClock(int val) {
			setSensorPin(CLOCK, val);
		}

		/**
		 * Set the data pin to the specified value
		 * 
		 * @param val
		 *            new value(0/1) for the pin.
		 */
		private void setData(int val) {
			setSensorPin(DATA, val);
		}

		/**
		 * get the current digital value from the data pin.
		 * 
		 * @return current pin value
		 */
		private boolean getData() {
			return getSensorPin(DATA) != 0;
		}

		/**
		 * Read the current analogue value from the data pin
		 * 
		 * @return current value of the pin.
		 */
		private int readData() {
			return readSensorPin(DATA);
		}

		/**
		 * perform a reset of the device.
		 */
		private void resetSensor() {
			// Set both ports to 1
			setClock(1);
			setData(1);
			setSensorPinMode(CLOCK, SensorPort.SP_MODE_OUTPUT);
			setSensorPinMode(DATA, SensorPort.SP_MODE_OUTPUT);
			Delay.msDelay(1);
			// Take clock down
			setClock(0);
			Delay.msDelay(1);
			// Raise it
			setClock(1);
			Delay.msDelay(1);
			// Take clock down for 100ms
			setClock(0);
			Delay.msDelay(100);
		}

		/**
		 * Send the new operating mode to the sensor. The value is sent to the
		 * sensor by using the clock pin to clock a series of 8 bits out to the
		 * device.
		 * 
		 * @param mode
		 */
		private void sendMode(int mode) {
			for (int i = 0; i < 8; i++) {
				// Raise clock
				setClock(1);
				// Set the data
				setData(mode & 1);
				Delay.usDelay(30);
				// Drop the clock
				setClock(0);
				mode >>= 1;
				Delay.usDelay(30);
			}
		}

		/**
		 * Read a data byte from the sensor. The data is read by reading the
		 * digital value of the data pin while using the clock pin to request
		 * each of the 8 bits.
		 * 
		 * @return The read byte.
		 */
		private int readByte() {
			/*
			 * NOTE: This code has been modified to remove the delays and to use
			 * a direct native method call. Even with these changes it is still
			 * slower then the timings that are given in the commented code.
			 * Basically leJOS is currently too slow to meet these timings. The
			 * original code is left commented out in the hope that at some
			 * point leJOS will be fast enough to need it!
			 */
			int val = 0;
			for (int i = 0; i < 8; i++) {
				setSensorPin(iPortId, CLOCK, 1);
				// setClock(1);
				// Delay.usDelay(4);
				val >>= 1;
				if (getData())
					val |= 0x80;
				setSensorPin(iPortId, CLOCK, 0);
				// setClock(0);
				// Delay.usDelay(4);
			}
			return val;
		}

		/**
		 * Incrementally calculate the CRC value of the read data.
		 * 
		 * @param crc
		 *            current crc
		 * @param val
		 *            new value
		 * @return new crc
		 */
		private int calcCRC(int crc, int val) {
			for (int i = 0; i < 8; i++) {
				if (((val ^ crc) & 1) != 0)
					crc = ((crc >>> 1) ^ 0xa001);
				else
					crc >>>= 1;
				val >>>= 1;
			}
			return crc & 0xffff;
		}

		/**
		 * Read the calibration data from the sensor. This consists of two
		 * tables. The first contains 3 rows of data with each row having 4
		 * columns. The data is sent one row at a time. Each row contains a
		 * calibration constant for red/green/blue/blank readings. The second
		 * table contains 2 threshold values that are used (based on the
		 * background light reading) to select the row to use from the first
		 * table. Finally there is a CRC value which is used to ensure correct
		 * reading of the data.
		 * 
		 * @return true if ok false if error
		 */
		private boolean readCalibration() {
			setSensorPinMode(DATA, SensorPort.SP_MODE_INPUT);
			int crcVal = 0x5aa5;
			int input;
			for (int i = 0; i < calData.length; i++)
				for (int col = 0; col < calData[i].length; col++) {
					int val = 0;
					int shift = 0;
					for (int k = 0; k < 4; k++) {
						input = readByte();
						crcVal = calcCRC(crcVal, input);
						val |= input << shift;
						shift += 8;
					}
					calData[i][col] = val;
					// RConsole.println("entry " + i + " col " + col + " value "
					// + val);
				}
			for (int i = 0; i < calLimits.length; i++) {
				int val = 0;
				int shift = 0;
				for (int k = 0; k < 2; k++) {
					input = readByte();
					crcVal = calcCRC(crcVal, input);
					val |= input << shift;
					shift += 8;
				}
				// RConsole.println("limit " + i + " value " + val);
				calLimits[i] = val;
			}
			int crc = (readByte() << 8);
			crc += readByte();
			crc &= 0xffff;
			setSensorPinMode(DATA, SensorPort.SP_MODE_ADC);
			Delay.msDelay(1);
			/*
			 * if (crc != crcVal) { LCD.clear(); for (int i = 0; i < 4; i++)
			 * for(int j = 0; j < 3; j++)
			 * LCD.drawString(Integer.toHexString(calData[j][i]), j*5, i);
			 * LCD.drawString(Integer.toHexString(calLimits[0]), 0, 5);
			 * LCD.drawString(Integer.toHexString(calLimits[1]), 8, 5);
			 * 
			 * LCD.drawString(Integer.toHexString(crc), 0, 6);
			 * LCD.drawString(Integer.toHexString(crcVal), 8, 6);
			 * Delay.msDelay(10000); }
			 */
			return crc == crcVal;
		}

		/**
		 * Initialize the sensor and set the operating mode.
		 * 
		 * @param mode
		 *            Operating mode.
		 * @return true if ok false if error.
		 */
		private boolean initSensor(int mode) {
			resetSensor();
			sendMode(mode);
			return readCalibration();
		}

		/**
		 * Check to see if a sensor is attached and working, Read the standard
		 * sensor analogue pin to see if a the sensor is present. If it is it
		 * will pull this pin down. If the sensor is detected but it has not
		 * been initialized then initialize it.
		 * 
		 * @return true if sensor is connected and working false otherwise.
		 */
		private boolean checkInitialized() {
			// is there a sensor attached?
			int ANAValue = readSensorPin(SensorPort.SP_ANA);
			if (ANAValue > 50)
				initialized = false;
			else if (!initialized)
				initialized = initSensor(type);
			return initialized;
		}

		/**
		 * Check the state of an initialized sensor. Once initialized this
		 * method will check that the sensor is not reporting an error state.
		 * The sensor can do this by pulling the clock pin high
		 * 
		 * @return true if ok false if error.
		 */
		private boolean checkSensor() {
			setSensorPinMode(CLOCK, SensorPort.SP_MODE_INPUT);
			Delay.msDelay(2);
			if (getSensorPin(CLOCK) != 0)
				initialized = false;
			return initialized;
		}

		/**
		 * Read a value from the sensor when in fill color mode. When in full
		 * color mode the readings are taken by toggling the clock line to move
		 * from one reading to the next. This method performs this operation. It
		 * also samples the analogue value twice and returns the average
		 * reading.
		 * 
		 * @param newClock
		 *            New value for the clock pin
		 * @return the new reading
		 */
		private int readFullColorValue(int newClock) {
			// delayUS(40);
			int val = readSensorPin(DATA);// readData();
			// delayUS(40);
			int val2 = readSensorPin(DATA);// readData();
			// val = (val + readData())/2;
			setClock(newClock);
			return (val + val2) / 2;
		}

		/**
		 * Read the device
		 * 
		 * @return true if ok false if error
		 */
		private boolean readSensor() {
			if (!checkInitialized())
				return false;
			if (type == TYPE_COLORFULL) {
				if (!checkSensor())
					return false;
				setSensorPinMode(CLOCK, SensorPort.SP_MODE_OUTPUT);
				rawValues[BLANK_INDEX] = readFullColorValue(1);
				rawValues[RED_INDEX] = readFullColorValue(0);
				rawValues[GREEN_INDEX] = readFullColorValue(1);
				rawValues[BLUE_INDEX] = readFullColorValue(0);
				return true;
			} else {
				if (!checkSensor())
					return false;
				rawValues[type - TYPE_COLORRED] = readData();
				return true;
			}
		}

		/**
		 * Return a single raw value from the device. When in single color mode
		 * this returns the raw sensor reading. Values range from 0 to 1023 but
		 * usually don't get over 600.
		 * 
		 * @return the raw value or < 0 if there is an error.
		 */
		@Override
		public int readRawValue() {
			if (type < TYPE_COLORRED)
				return -1;
			if (!readSensor())
				return -1;
			return rawValues[type - TYPE_COLORRED];
		}

		/**
		 * When in full color mode this returns all four raw color values from
		 * the device by doing four very quick reads and flashing all colors.
		 * The raw values theoretically range from 0 to 1023 but in practice
		 * they usually do not go higher than 600. You can access the index of
		 * each color using RGB_RED, RGB_GREEN, RGB_BLUE and RGB_BLANK. e.g. to
		 * retrieve the Blue value: <code>vals[ColorSensor.RGB_BLUE]</code>
		 * 
		 * @param vals
		 *            array of four color values.
		 * @return < 0 if there is an error the number of values if ok
		 */
		@Override
		public int readRawValues(int[] vals) {
			if (type != TYPE_COLORFULL)
				return -1;
			if (!readSensor())
				return -1;
			System.arraycopy(rawValues, 0, vals, 0, rawValues.length);
			return rawValues.length;
		}

		/**
		 * This method accepts a set of raw values (in full color mode) and
		 * processes them using the calibration data to return standard RGB
		 * values between 0 and 255
		 * 
		 * @param vals
		 *            array to return the newly calibrated data.
		 */
		private void calibrate(int[] vals) {
			// First select the calibration table to use...
			int calTab;
			int blankVal = rawValues[BLANK_INDEX];
			if (blankVal < calLimits[1])
				calTab = 2;
			else if (blankVal < calLimits[0])
				calTab = 1;
			else
				calTab = 0;
			// Now adjust the raw values
			for (int col = RED_INDEX; col <= BLUE_INDEX; col++)
				if (rawValues[col] > blankVal)
					vals[col] = ((rawValues[col] - blankVal) * calData[calTab][col]) >>> 16;
				else
					vals[col] = 0;
			// finally adjust the blank value
			if (blankVal > MINBLANKVAL)
				blankVal -= MINBLANKVAL;
			else
				blankVal = 0;
			blankVal = (blankVal * 100)
					/ (((SENSORMAX - MINBLANKVAL) * 100) / ADMAX);
			vals[BLANK_INDEX] = (blankVal * calData[calTab][BLANK_INDEX]) >>> 16;
		}

		/**
		 * Return a set of calibrated data. If in single color mode the returned
		 * data is a simple percentage. If in full color mode the data is a set
		 * of calibrated red/blue/green/blank readings that range from 0 to 255.
		 * You can access the index of each color using RED_INDEX, GREEN_INDEX,
		 * BLUE_INDEX and BLANK_INDEX. e.g. to retrieve the Blue value:
		 * <code>vals[ColorSensor.BLUE_INDEX]</code>
		 * 
		 * @param vals
		 *            4 element array for the results
		 * @return < 0 of error, the number of values if ok
		 */
		@Override
		public int readValues(int[] vals) {
			if (type != TYPE_COLORFULL)
				return -1;
			if (!readSensor())
				return -1;
			calibrate(vals);
			return BLANK_INDEX + 1;
		}

		/**
		 * Return a single processed value. If in single color mode this returns
		 * a single reading as a percentage. If in full color mode it returns a
		 * Lego color value that identifies the color of the object in view.
		 * 
		 * @return processed color value.
		 */
		@Override
		public int readValue() {
			if (!readSensor())
				return -1;
			if (type >= TYPE_COLORRED)
				return (rawValues[type - TYPE_COLORRED] * 100) / SENSORMAX;
			else {
				calibrate(values);
				int red = values[RED_INDEX];
				int blue = values[BLUE_INDEX];
				int green = values[GREEN_INDEX];
				int blank = values[BLANK_INDEX];
				// we have calibrated values, now use them to determine the
				// color
				/*
				 * This is the original color recognition algorithm taken from
				 * the 1.28 version of the Lego firmware if ((red < 55 && green
				 * < 55 && blue < 55) || (blank < 30 && red < 100 && green < 100
				 * && blue < 100)) return SensorPort.BLACK; if (red > blue &&
				 * red > green) { // red dominant color if (((blue >> 1) + (blue
				 * >> 2) + blue < green) && (green << 1) + green > red) return
				 * SensorPort.YELLOW; if ((green << 1) < red) return
				 * SensorPort.RED; if (blue < 70 || green < 70 || (blank < 100
				 * && red < 100)) return SensorPort.BLACK; return
				 * SensorPort.WHITE; } else if (green > blue) { // green
				 * dominant if ((blue << 1) < red) return SensorPort.YELLOW; if
				 * ((red + (red >> 2) + (red >> 3) < green) || (blue + (blue >>
				 * 2) + (blue >> 3) < green)) return SensorPort.GREEN; if (red <
				 * 70 || blue < 70 || (blank < 100 && green < 100)) return
				 * SensorPort.BLACK; return SensorPort.WHITE; } else { // Blue
				 * is dominant if ((red + (red >> 3) + (red >> 4) < blue) ||
				 * (green + green >> 3) + (green >> 4) < blue) return
				 * SensorPort.BLUE; if (red < 70 || green < 70 || (blank < 100
				 * && blue < 100)) return SensorPort.BLACK; return
				 * SensorPort.WHITE; }
				 */
				// The following algorithm comes from the 1.29 Lego firmware.
				if (red > blue && red > green) {
					// red dominant color
					if (red < 65 || (blank < 40 && red < 110))
						return SensorPort.BLACK;
					if (((blue >> 2) + (blue >> 3) + blue < green)
							&& ((green << 1) > red))
						return SensorPort.YELLOW;
					if ((green << 1) - (green >> 2) < red)
						return SensorPort.RED;
					if (blue < 70 || green < 70 || (blank < 140 && red < 140))
						return SensorPort.BLACK;
					return SensorPort.WHITE;
				} else if (green > blue) {
					// green dominant color
					if (green < 40 || (blank < 30 && green < 70))
						return SensorPort.BLACK;
					if ((blue << 1) < red)
						return SensorPort.YELLOW;
					if ((red + (red >> 2)) < green
							|| (blue + (blue >> 2)) < green)
						return SensorPort.GREEN;
					if (red < 70 || blue < 70 || (blank < 140 && green < 140))
						return SensorPort.BLACK;
					return SensorPort.WHITE;
				} else {
					// blue dominant color
					if (blue < 48 || (blank < 25 && blue < 85))
						return SensorPort.BLACK;
					if ((((red * 48) >> 5) < blue && ((green * 48) >> 5) < blue)
							|| ((red * 58) >> 5) < blue
							|| ((green * 58) >> 5) < blue)
						return SensorPort.BLUE;
					if (red < 60 || green < 60 || (blank < 110 && blue < 120))
						return SensorPort.BLACK;
					if ((red + (red >> 3)) < blue
							|| (green + (green >> 3)) < blue)
						return SensorPort.BLUE;
					return SensorPort.WHITE;
				}
			}
		}
	}

	private final SensorReader offReader = new SensorReader();
	private final SensorReader standardReader = new StandardReader();
	private SensorReader colorReader = null;
	private SensorReader curReader = offReader;

	/**
	 * Enable the use of the Color Light Sensor on this port. The code for this
	 * sensor is relatively large, so it is not presnt by default. Calling this
	 * function will enable this code. NOTE: Calling this function will reset
	 * the port. If you are using higher level interfaces (like the ColorSensor
	 * class, then this call will be made automatically.).
	 */
	public void enableColorSensor() {
		if (colorReader != null)
			return;
		colorReader = new ColorSensorReader();
		reset();
	}

	private SensorPort(int aId, SimulatorInterface.Sensor sensor) {
		this.iPortId = aId;
		this.sensor = sensor;
		reset();
	}

	/**
	 * Reset this port and attempt to reset any attached device.
	 */
	public void reset() {
		// reset all known sensor types
		standardReader.reset();
		if (colorReader != null)
			colorReader.reset();
		// force re-selection of reader
		type = -1;
		mode = MODE_RAW;
		curReader = offReader;
		// set default listener filter
		listenerFilter = ((1 << iPortId) << GT_EVENT_SHIFT)
				| ((1 << iPortId) << LT_EVENT_SHIFT)
				| (DEFAULT_TOLERANCE << TOLERANCE_SHIFT);
		setType(TYPE_NO_SENSOR);
	}

	/**
	 * Return the ID of the port. One of 0, 1, 2 or 3.
	 * 
	 * @return The Id of this sensor
	 */
	public final int getId() {
		return iPortId;
	}

	/**
	 * Adds a port listener.
	 * <p>
	 * <b> NOTE 1: You can add at most 8 listeners.<br>
	 * NOTE 2: Synchronizing inside listener methods could result in a deadlock.
	 * </b>
	 * 
	 * @param aListener
	 *            Listener for call backs
	 * @see lejos.nxt.SensorPortListener
	 */
	public synchronized void addSensorPortListener(SensorPortListener aListener) {
		if (iListeners == null) {
			iListeners = new SensorPortListener[8];
			ListenerThread.get().addListener(NXTEvent.ANALOG_PORTS,
					listenerFilter, 4, this);
		}
		iListeners[iNumListeners++] = aListener;
	}

	/**
	 * Set the tolerance used when triggering a listener event. The event will
	 * only trigger when the new value is different from the old value by +/-
	 * this amount.
	 * 
	 * @param tolerance
	 */
	public synchronized void setListenerTolerance(int tolerance) {
		listenerFilter = (listenerFilter & ~TOLERANCE_MASK)
				| ((tolerance << TOLERANCE_SHIFT) & TOLERANCE_MASK);
	}

	/**
	 * Activates an RCX sensor. This method should be called if you want to get
	 * accurate values from an RCX sensor. In the case of RCX light sensors, you
	 * should see the LED go on when you call this method.
	 */
	public final void activate() {
		setPowerType(1);
	}

	/**
	 * Passivates an RCX sensor.
	 */
	public final void passivate() {
		setPowerType(0);
	}

	/**
	 * Returns mode compatible with Lego firmware.
	 * 
	 * @return the current mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Returns type compatible with Lego firmware.
	 * 
	 * @return The type of the sensor
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets type and mode compatible with Lego firmware.
	 * 
	 * @param type
	 *            the sensor type
	 * @param mode
	 *            the sensor mode
	 */
	public void setTypeAndMode(int type, int mode) {
		setType(type);
		setMode(mode);
	}

	/**
	 * Sets type compatible with Lego firmware.
	 * 
	 * @param newType
	 *            the sensor type
	 */
	public void setType(int newType) {
		if (newType == type)
			return;
		if (newType < MIN_TYPE || newType > MAX_TYPE)
			throw new IllegalArgumentException();

		int powerType;
		switch (newType) {
		case TYPE_TEMPERATURE:
		case TYPE_REFLECTION:
		case TYPE_ANGLE:
			powerType = POWER_RCX9V;
			break;
		case TYPE_LOWSPEED_9V:
			powerType = POWER_9V;
			break;
		default:
			powerType = POWER_STD;
		}

		int controlPins;
		switch (newType) {
		case TYPE_LIGHT_ACTIVE:
		case TYPE_SOUND_DB:
			controlPins = 1 << SP_DIGI0;
			break;
		case TYPE_SOUND_DBA:
			controlPins = 1 << SP_DIGI1;
			break;
		case TYPE_LOWSPEED:
		case TYPE_LOWSPEED_9V:
			controlPins = -1;
			break;
		default:
			controlPins = 0;
		}

		// Work out what reader we need for the new type
		SensorReader newReader;
		// Determine what reader to use.
		if (newType >= TYPE_COLORFULL)
			newReader = colorReader;
		else if (newType >= TYPE_SWITCH)
			newReader = standardReader;
		else
			newReader = offReader;
		if (newReader == null)
			newReader = offReader;
		// if we are changing readers tell the old one we are done.
		if (newReader != curReader)
			curReader.setType(TYPE_NO_SENSOR);
		// Set the power and pins for the new type.
		setPowerType(powerType);
		// Set the state of the digital I/O pins
		if (controlPins >= 0) {
			setSensorPinMode(SP_DIGI0, SP_MODE_OUTPUT);
			setSensorPinMode(SP_DIGI1, SP_MODE_OUTPUT);
			setSensorPin(SP_DIGI0, (controlPins >> SP_DIGI0) & 0x01);
			setSensorPin(SP_DIGI1, (controlPins >> SP_DIGI1) & 0x01);
		}
		// Switch to the new type
		this.type = newType;
		curReader = newReader;
		newReader.setType(newType);
		newReader.setMode(mode);
	}

	/**
	 * Sets mode compatible with Lego firmware.
	 * 
	 * @param mode
	 *            the mode to set.
	 */
	public void setMode(int mode) {
		this.mode = mode;
		curReader.setMode(mode);
	}

	/**
	 * Reads the raw value of the sensor.
	 * 
	 * @return the raw sensor value
	 */
	public final int readRawValue() {
		return curReader.readRawValue();
	}

	/**
	 * Returns value compatible with Lego firmware.
	 * 
	 * @return the computed value
	 */
	public int readValue() {
		return curReader.readValue();
	}

	/**
	 * Return a variable number of sensor values
	 * 
	 * @param values
	 *            An array in which to return the sensor values.
	 * @return The number of values returned.
	 */
	public int readValues(int[] values) {
		return curReader.readValues(values);
	}

	/**
	 * Return a variable number of raw sensor values
	 * 
	 * @param values
	 *            An array in which to return the sensor values.
	 * @return The number of values returned.
	 */
	public int readRawValues(int[] values) {
		return curReader.readRawValues(values);
	}

	/**
	 * Reads the boolean value of the sensor.
	 * 
	 * @return the boolean state of the sensor
	 */
	public final boolean readBooleanValue() {
		int rawValue = readRawValue();
		return (rawValue < 600);
	}

	/**
	 * <i>Low-level API</i> for reading sensor values. Currently always returns
	 * the raw ADC value.
	 * 
	 * @param aPortId
	 *            Port ID (0..4).
	 */
	private static int readSensorValue(int aPortId) {
		throw new NotImplementedException("SensorPort.readSensorValue");
	}

	/**
	 * Low-level method to set the input power setting for a sensor. Values are:
	 * 0 - no power, 1 RCX active power, 2 power always on.
	 * 
	 * @param type
	 *            Power type to use
	 */
	public void setPowerType(int type) {
		setPowerTypeById(iPortId, type);
	}

	/**
	 * Low-level method to set the input power setting for a sensor. Values are:
	 * 0 - no power, 1 RCX active power, 2 power always on.
	 **/
	private static void setPowerTypeById(int aPortId, int aPortType) {
	}

	/**
	 * Call Port Listeners. Used by ListenerThread.
	 * 
	 * @return New event filter mask.
	 */
	public synchronized int callListeners() {
		int newValue = readSensorValue(iPortId);
		for (int i = 0; i < iNumListeners; i++)
			iListeners[i].stateChanged(this, iPreviousValue, newValue);
		iPreviousValue = newValue;
		return (listenerFilter & ~TARGET_MASK) | (newValue << TARGET_SHIFT);
	}

	/**
	 * Low-level method to enable I2C on the port.
	 * 
	 * @param aPortId
	 *            The port number for this device
	 * @param mode
	 *            I/O mode to use
	 */
	private static void i2cEnableById(int aPortId, int mode) {
	}

	/**
	 * Low-level method to disable I2C on the port.
	 * 
	 * @param aPortId
	 *            The port number for this device
	 */
	private static void i2cDisableById(int aPortId) {
	}

	/**
	 * Low-level method to return the i2c port status
	 * 
	 * @param aPortId
	 *            The port number for this device
	 * @return 0 if ready -1: Invalid device -2: Device busy -3: Device fault
	 *         -4: Buffer size error. -5: Bus is busy
	 */
	private static int i2cStatusById(int aPortId) {
		throw new NotImplementedException("SensorPort.i2cStatusById");
	}

	/**
	 * Low-level method to start an I2C transaction.
	 * 
	 * @param aPortId
	 *            The port number for this device
	 * @param address
	 *            The I2C address of the device
	 * @param writeBuffer
	 *            The buffer for write operations
	 * @param writeOffset
	 *            Index of first byte to write
	 * @param writeLen
	 *            Number of bytes to write
	 * @param readLen
	 *            Number of bytes to read
	 * @return < 0 if there is an error
	 */
	private static int i2cStartById(int aPortId, int address,
			byte[] writeBuffer, int writeOffset, int writeLen, int readLen) {
		throw new NotImplementedException("SensorPort.i2cStartById");
	}

	/**
	 * Complete and I2C operation and retrieve any data read.
	 * 
	 * @param aPortId
	 *            The Port number for the device
	 * @param readBuffer
	 *            The buffer to be used for read operations
	 * @param offset
	 *            Index of first byte to read
	 * @param readLen
	 *            Number of bytes to read
	 * @return < 0 if the is an error, or number of bytes transferred
	 */
	private static int i2cCompleteById(int aPortId, byte[] readBuffer,
			int offset, int readLen) {
		throw new NotImplementedException("SensorPort.i2cCompleteById");
	}

	/**
	 * Low-level method to enable I2C on the port. Note because there can be
	 * multiple i2c sensors attached to a single port, only the first enable
	 * will set the operating mode.
	 * 
	 * @param mode
	 *            The operating mode for the device
	 */
	public synchronized void i2cEnable(int mode) {
		if (i2cSensorCnt++ == 0) {
			i2cEnableById(iPortId, mode);
			// Allocate the i2c wait event
			i2cEvent = NXTEvent.allocate(NXTEvent.I2C_PORTS, 1 << iPortId, 1);
			i2cHighSpeed = (mode & HIGH_SPEED) != 0;
		}
	}

	/**
	 * Low-level method to disable I2C on the port. Note if multiple i2c sensors
	 * are sharing the same port the port will not be disabled until all of the
	 * associated sensors have disabled the port.
	 * 
	 */
	public synchronized void i2cDisable() {
		if (--i2cSensorCnt == 0) {
			i2cDisableById(iPortId);
			i2cEvent.free();
			i2cEvent = null;
		}
	}

	/**
	 * Wait for the current IO operation on the i2c port to complete.
	 */
	public void i2cWaitIOComplete() {
		// No need to wait for high speed ports
		if (!i2cHighSpeed)
			try {
				i2cEvent.waitEvent(I2C_IO_COMPLETE << iPortId,
						NXTEvent.WAIT_FOREVER);
			} catch (InterruptedException e) {
				// TODO: Decide if this method should simply throw the exception
				// preserve state of interrupt flag
				Thread.currentThread().interrupt();
			}
	}

	/**
	 * Low-level method to test if I2C connection is busy.
	 * 
	 * @return 0 if ready -1: Invalid device -2: Device busy -3: Device fault
	 *         -4: Buffer size error. -5: Bus is busy
	 */
	public int i2cStatus() {
		return i2cStatusById(iPortId);
	}

	/**
	 * Low-level method to start an I2C transaction. Any data that is read is
	 * obtained via a call to i2cComplete.
	 * 
	 * @param address
	 *            Address of the device
	 * @param writeBuffer
	 *            Buffer containing bytes to write
	 * @param writeOffset
	 *            Offset into writeBuffer
	 * @param writeLen
	 *            number of bytes to write
	 * @param readLen
	 *            number of bytes to read
	 * @return < 0 error
	 */
	public int i2cStart(int address, byte[] writeBuffer, int writeOffset,
			int writeLen, int readLen) {
		return i2cStartById(iPortId, address, writeBuffer, writeOffset,
				writeLen, readLen);
	}

	/**
	 * Complete an I2C operation and transfer any read bytes
	 * 
	 * @param buffer
	 *            Buffer for read data
	 * @param offset
	 *            offset into the buffer
	 * @param numBytes
	 *            Number of bytes to read
	 * @return < 0 error otherwise number of bytes read.
	 */
	public int i2cComplete(byte[] buffer, int offset, int numBytes) {
		return i2cCompleteById(iPortId, buffer, offset, numBytes);
	}

	/**
	 * High level i2c interface. Perform a complete i2c transaction and return
	 * the results. Writes the specified data to the device and then reads the
	 * requested bytes from it.
	 * 
	 * @param deviceAddress
	 *            The I2C device address.
	 * @param writeBuf
	 *            The buffer containing data to be written to the device.
	 * @param writeOffset
	 *            The offset of the data within the write buffer
	 * @param writeLen
	 *            The number of bytes to write.
	 * @param readBuf
	 *            The buffer to use for the transaction results
	 * @param readOffset
	 *            Location to write the results to
	 * @param readLen
	 *            The length of the read
	 * @return < 0 error otherwise the number of bytes read
	 */
	public synchronized int i2cTransaction(int deviceAddress, byte[] writeBuf,
			int writeOffset, int writeLen, byte[] readBuf, int readOffset,
			int readLen) {
		int ret = i2cStartById(iPortId, deviceAddress, writeBuf, writeOffset,
				writeLen, readLen);
		try {
			if (ret == ERR_BUS_BUSY) {
				// The bus is busy (clock and/or data lines not pulled up to 1).
				// This could be because a sensor (Lego Ultrasonic) is holding
				// on to the bus, or because no sensor is plugged in. So we wait
				// for a short while for it to become free and try again.
				i2cEvent.waitEvent(I2C_BUS_FREE << iPortId,
						I2C_BUS_FREE_TIMEOUT);
				ret = i2cStartById(iPortId, deviceAddress, writeBuf,
						writeOffset, writeLen, readLen);
			}
			if (ret < 0)
				return ret;

			// No need to wait for high speed ports
			if (!i2cHighSpeed)
				i2cEvent.waitEvent(I2C_IO_COMPLETE << iPortId,
						NXTEvent.WAIT_FOREVER);
		} catch (InterruptedException e) {
			// TODO: Need to decide if this should simply throw the exception
			// preserve state of interrupt flag
			Thread.currentThread().interrupt();
			return ERR_ABORT;
		}

		return i2cCompleteById(iPortId, readBuf, readOffset, readLen);
	}

	/**
	 * Low level method to set the operating mode for a sensor pin.
	 * 
	 * @param port
	 *            The port number to use
	 * @param pin
	 *            The pin id
	 * @param mode
	 *            The new mode
	 */
	private static void setSensorPinMode(int port, int pin, int mode) {
	}

	/**
	 * Set the output state of a sensor pin
	 * 
	 * @param port
	 *            The port to use
	 * @param pin
	 *            The pin id
	 * @param val
	 *            The new output value (0/1)
	 */
	private static void setSensorPin(int port, int pin, int val) {
	}

	/**
	 * Read the current state of a sensor port pin
	 * 
	 * @param port
	 *            The port to read
	 * @param pin
	 *            The pin id.
	 * @return The current pin state (0/1)
	 */
	private static int getSensorPin(int port, int pin) {
		throw new NotImplementedException("SensorPort.getSensorPin");
	}

	/**
	 * Read the current ADC value from a sensor port pin
	 * 
	 * @param port
	 *            The port to use.
	 * @param pin
	 *            The id of the pin to read (SP_DIGI1/SP_ANA)
	 * @return The return from the ADC
	 */
	private static int readSensorPin(int port, int pin) {
		throw new NotImplementedException("SensorPort.readSensorPin");
	}

	/**
	 * Low level method to set the operating mode for a sensor pin.
	 * 
	 * @param pin
	 *            The pin id
	 * @param mode
	 *            The new mode
	 */
	public void setSensorPinMode(int pin, int mode) {
		setSensorPinMode(iPortId, pin, mode);
	}

	/**
	 * Set the output state of a sensor pin
	 * 
	 * @param pin
	 *            The pin id
	 * @param val
	 *            The new output value (0/1)
	 */
	public void setSensorPin(int pin, int val) {
		setSensorPin(iPortId, pin, val);
	}

	/**
	 * Read the current state of a sensor port pin
	 * 
	 * @param pin
	 *            The pin id.
	 * @return The current pin state (0/1)
	 */
	public int getSensorPin(int pin) {
		return getSensorPin(iPortId, pin);
	}

	/**
	 * Read the current ADC value from a sensor port pin
	 * 
	 * @param pin
	 *            The id of the pin to read (SP_DIGI1/SP_ANA)
	 * @return The return from the ADC
	 */
	public int readSensorPin(int pin) {
		return readSensorPin(iPortId, pin);
	}

	@Override
	public <SensorType extends SimulatorInterface.Sensor> SensorType getSensor(
			Class<SensorType> sensorType) {
		if (sensor != null) {
			if (sensorType.isAssignableFrom(sensor.getClass())) {
				return sensorType.cast(sensor);
			} else {
				throw new RuntimeException("The sensor attached to port S"
						+ (iPortId + 1) + " is the wrong type");
			}
		} else {
			throw new RuntimeException("No sensor attached to port S"
					+ (iPortId + 1));
		}
	}
}
