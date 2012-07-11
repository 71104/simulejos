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
		public static final class Token {
		}

		void onPress(int buttonIndex);

		void onRelease(int buttonIndex);
	};

	static abstract class ButtonAdapter implements ButtonListener {
		@Override
		public void onPress(int buttonIndex) {
		}

		@Override
		public void onRelease(int buttonIndex) {
		}
	}

	ButtonListener.Token addButtonListener(ButtonListener listener);

	void removeButtonListener(ButtonListener.Token token);

	void dispose();
}
