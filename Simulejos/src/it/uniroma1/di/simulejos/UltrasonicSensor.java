package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;

final class UltrasonicSensor extends GPUSensor implements
		it.uniroma1.di.simulejos.bridge.SimulatorInterface.UltrasonicSensor {
	UltrasonicSensor(Robot robot, Vector3 position, Matrix3 heading) {
		robot.super(3, 3); // FIXME non è chiaro quale sia la risoluzione
	}
}
