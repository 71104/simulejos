package it.uniroma1.di.simulejos;

final class Clock {
	private final Object lock = new Object();
	private volatile long offset;
	private volatile boolean suspended;
	private volatile long suspendTimestamp;

	public long getTimestamp() {
		synchronized (lock) {
			if (suspended) {
				return suspendTimestamp - offset;
			} else {
				return System.nanoTime() - offset;
			}
		}
	}

	public void suspend() {
		synchronized (lock) {
			if (!suspended) {
				suspended = true;
				suspendTimestamp = System.nanoTime();
			}
		}
	}

	public void resume() {
		synchronized (lock) {
			if (suspended) {
				suspended = false;
				offset += System.nanoTime() - suspendTimestamp;
			}
		}
	}

	public void advance(int milliseconds) {
		synchronized (lock) {
			offset -= (long) milliseconds * 1000000;
		}
	}
}
