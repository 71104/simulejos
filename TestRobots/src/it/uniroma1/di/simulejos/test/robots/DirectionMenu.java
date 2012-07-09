package it.uniroma1.di.simulejos.test.robots;

import lejos.util.TextMenu;

public final class DirectionMenu {
	public static void main(String[] arguments) {
		final String[] items = { "forward", "backward", "left", "right" };
		final int selectedItem = new TextMenu(items, 1, "Direction:").select();
		if (selectedItem < 0) {
			System.out.println("no item selected");
		} else {
			System.out.println("you selected " + items[selectedItem]);
		}
	}
}
