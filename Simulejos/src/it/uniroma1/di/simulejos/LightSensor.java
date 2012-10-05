package it.uniroma1.di.simulejos;

import javax.media.opengl.GL2GL3;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.math.Vector3;

final class LightSensor extends GPUSensor implements
		it.uniroma1.di.simulejos.bridge.SimulatorInterface.LightSensor {
	private final Vector3 position;
	private final Vector3 heading;

	public LightSensor(Robot robot, Vector3 position, Vector3 heading) {
		robot.super(1, 1);
		this.position = position;
		this.heading = heading;
	}

	@Override
	public int getLight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void sample(GL2GL3 gl) {
		// TODO
	}
}
