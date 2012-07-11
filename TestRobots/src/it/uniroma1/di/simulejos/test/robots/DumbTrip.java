package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.Motor;

public final class DumbTrip {
	public static void main(String[] arguments) throws InterruptedException {
		Motor.A.forward();
		Thread.sleep(2000);
		Motor.B.forward();
		final Object blocker = new Object();
		synchronized (blocker) {
			while (true) {
				blocker.wait();
			}
		}
	}
}
