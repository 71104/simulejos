package it.uniroma1.di.simulejos;

import javax.media.opengl.GLAutoDrawable;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;

final class LightSensor extends GPUSensor implements
		SimulatorInterface.LightSensor {
	private final Vector3 position;
	private final Matrix3 heading;

	public LightSensor(Robot robot, Vector3 position, Matrix3 heading) {
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
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}
}
