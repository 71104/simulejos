package it.uniroma1.di.simulejos.bridge;

public interface BrickInterface {
	void updateDisplay(boolean[] data);

	boolean[] readDisplay();

	static final int ENTER_INDEX = 0;
	static final int LEFT_INDEX = 1;
	static final int RIGHT_INDEX = 2;
	static final int ESCAPE_INDEX = 3;

	int readButtons();

	static interface ButtonListener {
		void onPress(int buttonIndex);

		void onRelease(int buttonIndex);
	};

	Object addButtonListener(ButtonListener listener);

	void removeButtonListener(Object token);

	void dispose();
}
