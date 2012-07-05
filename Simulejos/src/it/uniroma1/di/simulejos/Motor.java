package it.uniroma1.di.simulejos;

import javax.script.ScriptException;

final class Motor implements
		it.uniroma1.di.simulejos.bridge.SimulatorInterface.Motor {
	private static final int RPM = 160;

	private volatile Mode mode = Mode.FLOAT;
	private volatile int power;
	private volatile double count;
	private volatile double offset;
	private volatile long lastUpdateTimestamp = System.currentTimeMillis();

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

	@Override
	public void control(int power, Mode mode) {
		final long timestamp = System.currentTimeMillis();
		if (mode == Mode.FORWARD) {
			count += (timestamp - lastUpdateTimestamp) * (power * RPM / 100.0)
					/ 60000.0;
		} else if (mode == Mode.BACKWARD) {
			count -= (timestamp - lastUpdateTimestamp) * (power * RPM / 100.0)
					/ 60000.0;
		}
		lastUpdateTimestamp = timestamp;
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

	public double tick() throws NoSuchMethodException, ScriptException {
		final double result = count - lastSample;
		lastSample = count;
		return result;
	}
}
