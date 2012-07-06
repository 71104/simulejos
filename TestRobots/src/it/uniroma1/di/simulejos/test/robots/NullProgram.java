package it.uniroma1.di.simulejos.test.robots;

public final class NullProgram {
	public static void main(String[] arguments) {
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
