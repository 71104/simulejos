package lejos.nxt;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import lejos.robotics.Color;
import lejos.robotics.LampLightDetector;

public class LightSensor implements LampLightDetector, SensorConstants {
	private final SimulatorInterface.LightSensor sensor;

	private int _zero = 1023;
	private int _hundred = 0;

	public LightSensor(ADSensorPort port) {
		this(port, true);
	}

	public LightSensor(ADSensorPort port, boolean floodlight) {
		this.sensor = port.getSensor(SimulatorInterface.LightSensor.class);
		this.sensor.setFloodLight(floodlight);
	}

	public void setFloodlight(boolean floodlight) {
		sensor.setFloodLight(floodlight);
	}

	public boolean setFloodlight(int color) {
		if (color == Color.RED) {
			sensor.setFloodLight(true);
			return true;
		} else if (color == Color.NONE) {
			sensor.setFloodLight(false);
			return true;
		} else {
			return false;
		}
	}

	public int getLightValue() {
		if (_hundred == _zero) {
			return 0;
		}
		return 100 * (sensor.getLight() - _zero) / (_hundred - _zero);
	}

	public int readValue() {
		return getLightValue();
	}

	public int readNormalizedValue() {
		return getNormalizedLightValue();
	}

	public int getNormalizedLightValue() {
		return 1023 - sensor.getLight();
	}

	public void calibrateLow() {
		_zero = sensor.getLight();
	}

	public void calibrateHigh() {
		_hundred = sensor.getLight();
	}

	public void setLow(int low) {
		_zero = 1023 - low;
	}

	public void setHigh(int high) {
		_hundred = 1023 - high;
	}

	public int getLow() {
		return 1023 - _zero;
	}

	public int getHigh() {
		return 1023 - _hundred;
	}

	public int getFloodlight() {
		if (sensor.isFloodLightOn()) {
			return Color.RED;
		} else {
			return Color.NONE;
		}
	}

	public boolean isFloodlightOn() {
		return sensor.isFloodLightOn();
	}
}
