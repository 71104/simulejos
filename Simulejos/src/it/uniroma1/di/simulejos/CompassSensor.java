package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.Robot.Sensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;

final class CompassSensor extends Sensor implements
		SimulatorInterface.CompassSensor {
	private final Matrix3 heading;
	private volatile double zero;

	public CompassSensor(Robot robot, Matrix3 heading) {
		robot.super();
		if (heading != null) {
			this.heading = heading;
		} else {
			this.heading = Matrix3.IDENTITY;
		}
	}

	private double getAbsoluteAngle() {
		final Vector3 needle = head(heading.by(Vector3.I));
		return Math.atan2(needle.z, needle.x);
	}

	@Override
	public double getAngle() {
		return (zero + getAbsoluteAngle()) % (Math.PI * 2);
	}

	@Override
	public double getCartesianAngle() {
		return (zero + Math.PI * 2 - getAbsoluteAngle()) % (Math.PI * 2);
	}

	@Override
	public void setZero() {
		zero = getAbsoluteAngle();
	}

	@Override
	public void resetZero() {
		zero = 0;
	}
}
