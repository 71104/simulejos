package lejos.nxt;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import lejos.robotics.Touch;

public class TouchSensor implements SensorConstants, Touch {
	private final SimulatorInterface.TouchSensor sensor;

	public TouchSensor(SensorPort port) {
		this.sensor = port.getSensor(SimulatorInterface.TouchSensor.class);
	}

	@Override
	public boolean isPressed() {
		return sensor.isPressed();
	}
}
