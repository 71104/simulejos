package it.uniroma1.di.simulejos.test.robots;

import java.io.PrintStream;

import lejos.nxt.Button;
import lejos.nxt.LCDOutputStream;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class TravelTest {
	private static final PrintStream SYSOUT = new PrintStream(
			new LCDOutputStream());
	private static volatile boolean stop = false;

	public static void main(String[] args) {
		final DifferentialPilot pilot = new DifferentialPilot(2.25f, 5.5f,
				Motor.A, Motor.B);
		final TouchSensor bumper = new TouchSensor(SensorPort.S1);
		SYSOUT.println("Press ENTER to start");
		Button.ENTER.waitForPressAndRelease();
		SYSOUT.print("going...");
		new Thread() {
			@Override
			public void run() {
				Button.ENTER.waitForPressAndRelease();
				stop = true;
			}
		}.start();
		pilot.travel(20, true);
		while (pilot.isMoving()) {
			if (stop || bumper.isPressed()) {
				pilot.stop();
			}
		}
		SYSOUT.print(" distance traveled: "
				+ pilot.getMovement().getDistanceTraveled());
		Button.waitForAnyPress();
	}
}
