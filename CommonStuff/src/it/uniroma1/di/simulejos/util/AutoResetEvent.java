package it.uniroma1.di.simulejos.util;

public final class AutoResetEvent<DataType> {
	private final Object mutex = new Object();
	private volatile Event<DataType> event = new Event<DataType>();

	public DataType waitEvent() {
		return event.waitEvent();
	}

	public DataType waitEvent(long timeout) {
		return event.waitEvent(timeout);
	}

	public DataType waitInterruptibleEvent() throws InterruptedException {
		return event.waitInterruptibleEvent();
	}

	public DataType waitInterruptibleEvent(long timeout)
			throws InterruptedException {
		return event.waitInterruptibleEvent(timeout);
	}

	public void signal(DataType data) {
		final Event<DataType> cache;
		synchronized (mutex) {
			cache = event;
			event = new Event<DataType>();
		}
		cache.signal(data);
	}
}
