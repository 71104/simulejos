package it.uniroma1.di.simulejos.ui;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.media.opengl.awt.GLJPanel;

final class Canvas extends GLJPanel {
	private static final long serialVersionUID = 4619660049948947766L;

	public Canvas(KeyAdapter keyboardHandler, MouseAdapter mouseHandler) {
		setPreferredSize(new Dimension(800, 600));
		setFocusable(true);
		addKeyListener(keyboardHandler);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				requestFocusInWindow();
			}
		});
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
		addMouseWheelListener(mouseHandler);
	}
}
