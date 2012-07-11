package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.Robot.GPUSensor;
import it.uniroma1.di.simulejos.bridge.SimulatorInterface;
import it.uniroma1.di.simulejos.math.Vector3;

final class ColorSensor extends GPUSensor implements
		SimulatorInterface.ColorSensor {
	private final Vector3 position;
	private final Vector3 heading;
	private volatile FloodLight floodLight = FloodLight.FULL;

	public ColorSensor(Robot robot, Vector3 position, Vector3 heading) {
		robot.super(1, 1);
		this.position = position;
		this.heading = heading;
	}

	@Override
	public int getColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FloodLight getFloodLight() {
		return floodLight;
	}

	@Override
	public void setFloodLight(FloodLight light) {
		if (light != null) {
			this.floodLight = light;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
