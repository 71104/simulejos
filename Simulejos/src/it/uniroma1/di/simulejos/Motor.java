package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.bridge.SimulatorInterface;

import javax.script.ScriptException;

final class Motor implements SimulatorInterface.Motor {
	private static final int RPM = 160;

	public static final class Timer {
		private volatile long offset;
		private volatile boolean suspended;
		private volatile long suspendTimestamp;

		private Timer() {
		}

		public synchronized long getTimestamp() {
			if (suspended) {
				return suspendTimestamp - offset;
			} else {
				return System.currentTimeMillis() - offset;
			}
		}

		public synchronized void suspend() {
			if (!suspended) {
				suspended = true;
				suspendTimestamp = System.currentTimeMillis();
			}
		}

		public synchronized void resume() {
			if (suspended) {
				suspended = false;
				offset += System.currentTimeMillis() - suspendTimestamp;
			}
		}
	}

	public final Timer timer = new Timer();

	private volatile Mode mode = Mode.FLOAT;
	private volatile int power;
	private volatile double count;
	private volatile double offset;
	private volatile long lastTimestamp = timer.getTimestamp();

	private volatile double lastSample;

	@Override
	public Mode getMode() {
		return mode;
	}

	@Override
	public void setMode(Mode mode) {
		control(this.power, mode);
	}

	@Override
	public int getPower() {
		return power;
	}

	@Override
	public void setPower(int power) {
		control(power, this.mode);
	}

	private double delta(long timestamp) {
		if (mode == Mode.FORWARD) {
			return (timestamp - lastTimestamp) * (power * RPM / 100.0)
					/ 60000.0;
		} else if (mode == Mode.BACKWARD) {
			return (lastTimestamp - timestamp) * (power * RPM / 100.0)
					/ 60000.0;
		} else {
			return 0;
		}
	}

	@Override
	public synchronized void control(int power, Mode mode) {
		final long timestamp = timer.getTimestamp();
		count += delta(timestamp);
		lastTimestamp = timestamp;
		this.power = power;
		this.mode = mode;
	}

	@Override
	public int getCount() {
		return (int) Math.round((count + offset) * 360);
	}

	@Override
	public void resetCount() {
		offset = -count;
	}

	public synchronized double tick() throws NoSuchMethodException,
			ScriptException {
		final double result = count + delta(timer.getTimestamp()) - lastSample;
		lastSample = count;
		return result;
	}
}
