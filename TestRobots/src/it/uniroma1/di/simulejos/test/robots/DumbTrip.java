package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.Motor;

public final class DumbTrip {
	public static void main(String[] arguments) {
		Motor.B.stop();
		Motor.A.forward();
		final Object blocker = new Object();
		synchronized (blocker) {
			while (true) {
				try {
					blocker.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
