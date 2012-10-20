package it.uniroma1.di.simulejos.util;

public final class BooleanAutoResetEvent {
	private final Object mutex = new Object();
	private volatile BooleanEvent event = new BooleanEvent();

	public void waitEvent() {
		event.waitEvent();
	}

	public boolean waitEvent(long timeout) {
		return event.waitEvent(timeout);
	}

	public void waitInterruptibleEvent() throws InterruptedException {
		event.waitInterruptibleEvent();
	}

	public boolean waitInterruptibleEvent(long timeout)
			throws InterruptedException {
		return event.waitInterruptibleEvent(timeout);
	}

	public void signal() {
		final BooleanEvent cache;
		synchronized (mutex) {
			cache = event;
			event = new BooleanEvent();
		}
		cache.signal();
	}
}
