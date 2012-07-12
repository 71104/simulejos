package lejos.nxt;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import lejos.robotics.*;

public class ColorSensor implements LampLightDetector, ColorDetector,
		SensorConstants {
	private static final int[] colorMap = { -1, Color.BLACK, Color.BLUE,
			Color.GREEN, Color.YELLOW, Color.RED, Color.WHITE };

	private final SimulatorInterface.ColorSensor sensor;
	private int zero = 1023;
	private int hundred = 0;

	public static class Color extends lejos.robotics.Color {
		private final int background;

		public Color(int red, int green, int blue, int background, int colorId) {
			super(red, green, blue, colorId);
			this.background = background;
		}

		public int getBackground() {
			return background;
		}
	}

	public ColorSensor(SensorPort port) {
		this(port, Color.WHITE);
	}

	public ColorSensor(SensorPort port, int color) {
		this.sensor = port.getSensor(SimulatorInterface.ColorSensor.class);
		setFloodlight(color);
	}

	protected void setType(int type) {
		switch (type) {
		case TYPE_COLORFULL:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.FULL);
			break;
		case TYPE_COLORRED:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.RED);
			break;
		case TYPE_COLORGREEN:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.GREEN);
			break;
		case TYPE_COLORBLUE:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.BLUE);
			break;
		case TYPE_COLORNONE:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.NONE);
			break;
		default:
			System.err
					.println("invalid flood light type specified to color sensor");
			break;
		}
	}

	public int getLightValue() {
		return sensor.getColor();
	}

	public int getNormalizedLightValue() {
		return getRawLightValue();
	}

	public int getRawLightValue() {
		return sensor.getColor();
	}

	public void setFloodlight(boolean floodlight) {
		setFloodlight(floodlight ? Color.RED : Color.NONE);
	}

	public ColorSensor.Color getColor() {
		final int reading = sensor.getColor();
		return new Color(reading & 0xFF, (reading & 0xFF00) >> 8,
				(reading & 0xFF0000) >> 16, 0, this.getColorID());
	}

	public ColorSensor.Color getRawColor() {
		final int reading = sensor.getColor();
		return new Color(reading & 0xFF, (reading & 0xFF00) >> 8,
				(reading & 0xFF0000) >> 16, 0, this.getColorID());
	}

	public int getFloodlight() {
		return sensor.getFloodLight().getColor();
	}

	public boolean isFloodlightOn() {
		return sensor.getFloodLight() != SimulatorInterface.ColorSensor.FloodLight.NONE;
	}

	public boolean setFloodlight(int color) {
		switch (color) {
		case Color.RED:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.RED);
			break;
		case Color.BLUE:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.BLUE);
			break;
		case Color.GREEN:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.GREEN);
			break;
		case Color.NONE:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.NONE);
			break;
		case Color.WHITE:
			sensor.setFloodLight(SimulatorInterface.ColorSensor.FloodLight.FULL);
			break;
		default:
			return false;
		}
		return true;
	}

	public void calibrateLow() {
		zero = sensor.getColor();
	}

	public void calibrateHigh() {
		hundred = sensor.getColor();
	}

	public void setLow(int low) {
		zero = 1023 - low;
	}

	public void setHigh(int high) {
		hundred = 1023 - high;
	}

	public int getLow() {
		return 1023 - zero;
	}

	public int getHigh() {
		return 1023 - hundred;
	}

	public int getColorID() {
		return colorMap[sensor.getColor()];
	}
}
