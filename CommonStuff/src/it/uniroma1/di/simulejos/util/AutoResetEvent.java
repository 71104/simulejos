package it.uniroma1.di.simulejos.util;

public final class AutoResetEvent<T> {
	private volatile Event<T> event = new Event<T>();

	public T waitEvent() {
		return event.waitEvent();
	}

	public T waitEvent(long timeout) {
		return event.waitEvent(timeout);
	}

	public T waitInterruptibleEvent() throws InterruptedException {
		return event.waitInterruptibleEvent();
	}

	public T waitInterruptibleEvent(long timeout) throws InterruptedException {
		return event.waitInterruptibleEvent(timeout);
	}

	public void notifyEvent(T data) {
		final Event<T> cache = event;
		event = new Event<T>();
		cache.notifyEvent(data);
	}
}
