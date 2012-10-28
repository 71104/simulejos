package it.uniroma1.di.simulejos;

import javax.media.opengl.GLAutoDrawable;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;

final class UltrasonicSensor extends GPUSensor implements
		SimulatorInterface.UltrasonicSensor {
	UltrasonicSensor(Robot robot, Vector3 position, Matrix3 heading) {
		robot.super(1, 1);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}
}
