package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public final class Example {
	public static void main(String[] args) {
		final DifferentialPilot pilot = new DifferentialPilot(2.25f, 5.5f,
				Motor.A, Motor.B);
		System.out.println("Press ENTER to start");
		Button.ENTER.waitForPressAndRelease();
		System.out.print("going...");
		pilot.travel(20, false);
	}
}
