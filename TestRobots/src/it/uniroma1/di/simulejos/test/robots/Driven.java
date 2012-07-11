package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.Button;
import lejos.nxt.Motor;

public final class Driven {
	private static volatile int currentSpeed = 100;

	private static abstract class ButtonHandler {
		public final Button button;

		public ButtonHandler(Button button) {
			this.button = button;
		}

		public void onPress() {
		}

		public void onRelease() {
		}
	}

	private static final ButtonHandler[] buttonHandlers = {
			new ButtonHandler(Button.LEFT) {
				@Override
				public void onPress() {
					Motor.B.setSpeed(currentSpeed * 2);
				}

				@Override
				public void onRelease() {
					Motor.B.setSpeed(currentSpeed);
				}
			}, new ButtonHandler(Button.RIGHT) {
				@Override
				public void onPress() {
					Motor.A.setSpeed(currentSpeed * 2);
				}

				@Override
				public void onRelease() {
					Motor.A.setSpeed(currentSpeed);
				}
			}, new ButtonHandler(Button.ENTER) {
				@Override
				public void onPress() {
					currentSpeed += 100;
					Motor.A.setSpeed(currentSpeed);
					Motor.B.setSpeed(currentSpeed);
				}
			}, new ButtonHandler(Button.ESCAPE) {
				@Override
				public void onPress() {
					currentSpeed = Math.max(currentSpeed - 100, 0);
					Motor.A.setSpeed(currentSpeed);
					Motor.B.setSpeed(currentSpeed);
				}
			} };

	public static void main(String[] arguments) throws InterruptedException {
		for (final ButtonHandler handler : buttonHandlers) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						handler.button.waitForPress();
						handler.onPress();
						handler.button.waitForPressAndRelease();
						handler.onRelease();
					}
				}
			}).start();
		}

		Motor.A.setSpeed(currentSpeed);
		Motor.B.setSpeed(currentSpeed);
		Motor.A.forward();
		Motor.B.forward();

		final Object blocker = new Object();
		synchronized (blocker) {
			while (true) {
				blocker.wait();
			}
		}
	}
}
