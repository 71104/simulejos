package it.uniroma1.di.simulejos.test.robots;

import lejos.nxt.Button;
import lejos.nxt.LCD;

public final class TestDraw {
	public static void main(String[] arguments) {
		LCD.drawChar('A', 0, 0);
		Button.ENTER.waitForPressAndRelease();
		LCD.clear();
		Button.ENTER.waitForPressAndRelease();
		LCD.drawString("HELLO", 0, 0);
		Button.ENTER.waitForPressAndRelease();
		LCD.drawString("HELLO, WORLD!", 0, 0, true);
	}
}
