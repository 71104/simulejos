package lejos.nxt;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import lejos.robotics.Touch;

public class TouchSensor implements SensorConstants, Touch {
	private final SimulatorInterface.TouchSensor sensor;

	public TouchSensor(SensorPort port) {
		final SimulatorInterface.Sensor sensor = port.getSensor();
		if (sensor != null) {
			if (sensor instanceof SimulatorInterface.TouchSensor) {
				this.sensor = (SimulatorInterface.TouchSensor) sensor;
			} else {
				throw new RuntimeException("The sensor attached to port S"
						+ (port.getId() + 1) + " is not a touch sensor");
			}
		} else {
			throw new RuntimeException("No sensor attached to port S"
					+ (port.getId() + 1));
		}
	}

	@Override
	public boolean isPressed() {
		return sensor.isPressed();
	}
}
