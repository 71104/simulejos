package it.uniroma1.di.simulejos;

import javax.media.opengl.GLAutoDrawable;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;

final class TouchSensor extends GPUSensor implements
		SimulatorInterface.TouchSensor {
	private final Vector3 position;
	private final Matrix3 heading;
	private final float size;

	public TouchSensor(Robot robot, Vector3 position, Matrix3 heading,
			float size) {
		robot.super(1, 1);
		this.position = position;
		this.heading = heading;
		this.size = size;
	}

	@Override
	public boolean isPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}
}
