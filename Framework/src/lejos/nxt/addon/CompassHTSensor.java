package lejos.nxt.addon;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.robotics.*;

public class CompassHTSensor extends I2CSensor implements DirectionFinder {
	private final SimulatorInterface.CompassSensor sensor;

	public CompassHTSensor(I2CPort port, int address) {
		super(port, address, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
		this.sensor = port.getSensor(SimulatorInterface.CompassSensor.class);
	}

	public CompassHTSensor(I2CPort port) {
		this(port, DEFAULT_I2C_ADDRESS);
	}

	public float getDegrees() {
		return (float) Math.toDegrees(sensor.getAngle());
	}

	public float getDegreesCartesian() {
		return (float) Math.toDegrees(sensor.getCartesianAngle());
	}

	public void resetCartesianZero() {
		sensor.resetZero();
	}

	public void startCalibration() {
	}

	public void stopCalibration() {
	}
}
