package it.uniroma1.di.simulejos;

import javax.media.opengl.GL2GL3;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Vector3;

final class TouchSensor extends GPUSensor implements
		SimulatorInterface.TouchSensor {
	private final Vector3 position;
	private final Vector3 heading;

	public TouchSensor(Robot robot, Vector3 position, Vector3 heading) {
		robot.super(1, 1);
		this.position = position;
		this.heading = heading;
	}

	@Override
	public boolean isPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void sample(GL2GL3 gl) {
		// TODO
	}
}
