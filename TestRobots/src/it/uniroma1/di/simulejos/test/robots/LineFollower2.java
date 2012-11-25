package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;

public final class LineFollower2 {
	public static void main(String[] arguments) {
		final LightSensor sensor = new LightSensor(SensorPort.S1);
		final DifferentialPilot pilot = new DifferentialPilot(1, 2, Motor.A,
				Motor.B);
		System.out.println("Press any key...");
		Button.waitForAnyPress();
		while (true) {
			int sweep = 5;
			while (sensor.readValue() > 40) {
				pilot.stop();
				pilot.rotate(sweep *= -2);
			}
			pilot.forward();
		}
	}
}
