package it.uniroma1.di.simulejos.bridge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import lejos.nxt.Button;

final class NXTWindow extends JDialog implements BrickInterface {
	private static final long serialVersionUID = 8971464747508405368L;

	private static final String[] ICON_NAMES = { "enter.png", "left.png",
			"right.png", "escape.png" };

	private static final int DISPLAY_WIDTH = 100;
	private static final int DISPLAY_HEIGHT = 64;
	private final boolean[] display = new boolean[DISPLAY_HEIGHT
			* DISPLAY_WIDTH];
	private final JPanel canvas = new JPanel() {
		private static final long serialVersionUID = 6593069734136873830L;

		{
			setPreferredSize(new Dimension(DISPLAY_WIDTH * 2,
					DISPLAY_HEIGHT * 2));
			setBackground(new Color(150, 220, 150));
		}

		@Override
		public void paintComponent(Graphics context) {
			super.paintComponent(context);
			context.setColor(Color.BLACK);
			for (int y = 0; y < DISPLAY_HEIGHT; y++) {
				for (int x = 0; x < DISPLAY_WIDTH; x++) {
					if (display[y * DISPLAY_WIDTH + x]) {
						context.fillRect(x * 2, y * 2, 2, 2);
					}
				}
			}
		}
	};

	private volatile int buttonState = 0;
	private final ConcurrentMap<Object, ButtonListener> buttonListeners = new ConcurrentHashMap<Object, ButtonListener>();

	NXTWindow(Frame parent, String name) {
		super(parent, name, false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setResizable(false);
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);

		final class Button extends AbstractAction {
			private static final long serialVersionUID = -8099406163811415439L;

			private final int index;

			public Button(int index) {
				this.index = index;
				putValue(
						LARGE_ICON_KEY,
						new ImageIcon((NXTWindow.class
								.getResource(ICON_NAMES[index]))));
			}

			@Override
			public void actionPerformed(ActionEvent event) {
				buttonState = buttonState | (1 << index);
				for (ButtonListener listener : buttonListeners.values()) {
					listener.onPress(index);
				}
				buttonState = buttonState & ~(1 << index);
				for (ButtonListener listener : buttonListeners.values()) {
					listener.onRelease(index);
				}
			}
		}

		final JToolBar buttons = new JToolBar(JToolBar.HORIZONTAL);
		buttons.add(new Button(LEFT_INDEX));
		buttons.add(new Button(ENTER_INDEX));
		buttons.add(new Button(RIGHT_INDEX));
		buttons.add(new Button(ESCAPE_INDEX));
		buttons.setFloatable(false);
		add(buttons, BorderLayout.SOUTH);

		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	@Override
	public void updateDisplay(boolean[] data) {
		synchronized (display) {
			System.arraycopy(data, 0, display, 0, display.length);
		}
		canvas.repaint();
	}

	@Override
	public boolean[] readDisplay() {
		return display.clone();
	}

	@Override
	public int readButtons() {
		return buttonState;
	}

	@Override
	public Object addButtonListener(ButtonListener listener) {
		Object token = new Object();
		buttonListeners.put(token, listener);
		return token;
	}

	@Override
	public void removeButtonListener(Object token) {
		buttonListeners.remove(token);
	}
}
