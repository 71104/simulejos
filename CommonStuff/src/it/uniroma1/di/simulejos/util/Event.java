package it.uniroma1.di.simulejos.util;

public final class Event<DataType> {
	private final Object blocker = new Object();
	private volatile DataType data;

	public DataType waitEvent() {
		synchronized (blocker) {
			while (data == null) {
				try {
					blocker.wait();
				} catch (InterruptedException e) {
				}
			}
			return data;
		}
	}

	public DataType waitEvent(long timeout) {
		synchronized (blocker) {
			long elapsed = System.currentTimeMillis();
			while ((data == null) && (timeout > 0)) {
				try {
					blocker.wait(timeout);
				} catch (InterruptedException e) {
				}
				long currentTime = System.currentTimeMillis();
				timeout -= currentTime - elapsed;
				elapsed = currentTime;
			}
			return data;
		}
	}

	public DataType waitInterruptibleEvent() throws InterruptedException {
		synchronized (blocker) {
			while (data == null) {
				blocker.wait();
			}
			return data;
		}
	}

	public DataType waitInterruptibleEvent(long timeout)
			throws InterruptedException {
		synchronized (blocker) {
			long elapsed = System.currentTimeMillis();
			while ((data == null) && (timeout > 0)) {
				blocker.wait(timeout);
				long currentTime = System.currentTimeMillis();
				timeout -= currentTime - elapsed;
				elapsed = currentTime;
			}
			return data;
		}
	}

	public void notifyEvent(DataType data) {
		if (data == null) {
			synchronized (blocker) {
				this.data = data;
				blocker.notifyAll();
			}
		}
	}
}
