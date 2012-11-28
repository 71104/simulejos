package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;

public final class Probe2 {
	public static void main(String[] arguments) {
		Motor.A.setSpeed(200);
		Motor.B.setSpeed(200);
		Motor.A.forward();
		Motor.B.forward();
		final LightSensor sensor = new LightSensor(SensorPort.S1);
		final Object blocker = new Object();
		while (true) {
			if (sensor.readNormalizedValue() >= 512) {
				LCD.bitBlt(null, LCD.SCREEN_WIDTH, LCD.SCREEN_HEIGHT, 0, 0, 0,
						0, LCD.SCREEN_WIDTH, LCD.SCREEN_HEIGHT, LCD.ROP_CLEAR);
			} else {
				LCD.bitBlt(null, LCD.SCREEN_WIDTH, LCD.SCREEN_HEIGHT, 0, 0, 0,
						0, LCD.SCREEN_WIDTH, LCD.SCREEN_HEIGHT, LCD.ROP_SET);
			}
			synchronized (blocker) {
				try {
					blocker.wait(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
