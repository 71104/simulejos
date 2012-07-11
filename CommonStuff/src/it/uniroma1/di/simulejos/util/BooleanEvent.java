package it.uniroma1.di.simulejos.util;

public final class BooleanEvent {
	private final Object blocker = new Object();
	private volatile boolean occur;

	public void waitEvent() {
		synchronized (blocker) {
			while (!occur) {
				try {
					blocker.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public boolean waitEvent(long timeout) {
		synchronized (blocker) {
			long elapsed = System.currentTimeMillis();
			while (!occur && (timeout > 0)) {
				try {
					blocker.wait(timeout);
				} catch (InterruptedException e) {
				}
				long currentTime = System.currentTimeMillis();
				timeout -= currentTime - elapsed;
				elapsed = currentTime;
			}
			return occur;
		}
	}

	public void waitInterruptibleEvent() throws InterruptedException {
		synchronized (blocker) {
			while (!occur) {
				blocker.wait();
			}
		}
	}

	public boolean waitInterruptibleEvent(long timeout)
			throws InterruptedException {
		synchronized (blocker) {
			long elapsed = System.currentTimeMillis();
			while (!occur && (timeout > 0)) {
				blocker.wait(timeout);
				long currentTime = System.currentTimeMillis();
				timeout -= currentTime - elapsed;
				elapsed = currentTime;
			}
			return occur;
		}
	}

	public void notifyEvent() {
		synchronized (blocker) {
			occur = true;
			blocker.notifyAll();
		}
	}
}
