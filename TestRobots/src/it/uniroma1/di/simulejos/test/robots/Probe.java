package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.util.DebugMessages;

public final class Probe {
	public static void main(String[] arguments) {
		Motor.A.setSpeed(50);
		Motor.B.setSpeed(50);
		Motor.A.forward();
		Motor.B.forward();
		final LightSensor sensor = new LightSensor(SensorPort.S1);
		final DebugMessages messages = new DebugMessages();
		final Object blocker = new Object();
		while (true) {
			messages.echo(sensor.readValue());
			synchronized (blocker) {
				try {
					blocker.wait(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
