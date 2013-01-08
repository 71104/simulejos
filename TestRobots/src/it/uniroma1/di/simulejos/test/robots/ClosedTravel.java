package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public final class ClosedTravel {
	public static void main(String[] arguments) {
		final DifferentialPilot pilot = new DifferentialPilot(2, 2, Motor.A,
				Motor.B);
		System.out.println("Press any key to start...");
		Button.waitForAnyPress();
		pilot.arc(4, 360, false);
	}
}
