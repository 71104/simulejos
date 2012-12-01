package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;

final class Accelerometer implements
		it.uniroma1.di.simulejos.bridge.SimulatorInterface.Accelerometer {
	private volatile Vector3 tilt = new Vector3(0, -1, 0);
	private volatile Vector3 velocity = Vector3.NULL;
	private volatile Vector3 acceleration = Vector3.NULL;

	public Accelerometer(Matrix3 heading) {
	}

	void move(long dt, double dx, double dy, double dz) {
		// TODO tilt
		final Vector3 newVelocity = new Vector3(dx / dt, dy / dt, dz / dt);
		acceleration = newVelocity.minus(velocity).div(dt);
		velocity = newVelocity;
	}

	@Override
	public Vector3 getTilt() {
		return tilt;
	}

	@Override
	public Vector3 getAcceleration() {
		return acceleration;
	}
}
