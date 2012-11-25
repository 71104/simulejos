package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import lejos.robotics.DirectionFinder;
import lejos.util.DebugMessages;

public final class Driven {
	private static volatile int currentSpeed = 100;
	private static final DirectionFinder compass = new CompassHTSensor(
			SensorPort.S1);

	private static final DebugMessages messages = new DebugMessages();

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
					messages.echo("left");
				}

				@Override
				public void onRelease() {
					Motor.B.setSpeed(currentSpeed);
					messages.echo("forward");
				}
			}, new ButtonHandler(Button.RIGHT) {
				@Override
				public void onPress() {
					Motor.A.setSpeed(currentSpeed * 2);
					messages.echo("right");
				}

				@Override
				public void onRelease() {
					Motor.A.setSpeed(currentSpeed);
					messages.echo("forward");
				}
			}, new ButtonHandler(Button.ENTER) {
				@Override
				public void onPress() {
					currentSpeed += 100;
					Motor.A.setSpeed(currentSpeed);
					Motor.A.forward();
					Motor.B.setSpeed(currentSpeed);
					Motor.B.forward();
					messages.echo("faster");
				}
			}, new ButtonHandler(Button.ESCAPE) {
				@Override
				public void onPress() {
					currentSpeed = Math.max(currentSpeed - 100, 0);
					Motor.A.setSpeed(currentSpeed);
					Motor.B.setSpeed(currentSpeed);
					messages.echo("slower");
				}
			} };

	private static void drawLine(int x0, int y0, int x1, int y1) {
		if (Math.abs(y1 - y0) > Math.abs(x1 - x0)) {
			if (y0 > y1) {
				for (int y = y1; y < y0; y++) {
					LCD.setPixel(x0 + (y - y0) * (x1 - x0) / (y1 - y0), y, 1);
				}
			} else {
				for (int y = y0; y < y1; y++) {
					LCD.setPixel(x0 + (y - y0) * (x1 - x0) / (y1 - y0), y, 1);
				}
			}
		} else {
			if (x0 > x1) {
				for (int x = x1; x < x0; x++) {
					LCD.setPixel(x, y0 + (x - x0) * (y1 - y0) / (x1 - x0), 1);
				}
			} else {
				for (int x = x0; x < x1; x++) {
					LCD.setPixel(x, y0 + (x - x0) * (y1 - y0) / (x1 - x0), 1);
				}
			}
		}
	}

	private static void drawCompass() {
		final int x0 = LCD.SCREEN_WIDTH / 2;
		final int y0 = LCD.SCREEN_HEIGHT / 2;
		final double length = LCD.SCREEN_HEIGHT * 0.475;
		LCD.clear();
		final double angle = Math.toRadians(compass.getDegreesCartesian())
				+ Math.PI / 2;
		final int x1 = x0 + (int) Math.round(Math.cos(angle) * length);
		final int y1 = y0 + (int) Math.round(Math.sin(angle) * length);
		drawLine(x0, LCD.SCREEN_HEIGHT - y0, x1, LCD.SCREEN_HEIGHT - y1);
	}

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

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					drawCompass();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}).start();

		final Object blocker = new Object();
		synchronized (blocker) {
			while (true) {
				blocker.wait();
			}
		}
	}
}
