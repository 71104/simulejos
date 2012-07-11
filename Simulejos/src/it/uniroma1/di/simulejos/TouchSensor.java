package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.Robot.Sensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Vector3;

final class TouchSensor extends Sensor implements
		SimulatorInterface.TouchSensor {
	private final Vector3 position;
	private final Vector3 heading;

	public TouchSensor(Robot robot, Vector3 position, Vector3 heading) {
		robot.super();
		this.position = position;
		this.heading = heading;
	}

	@Override
	public boolean isPressed() {
		// TODO Auto-generated method stub
		return false;
	}
}
