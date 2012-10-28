package it.uniroma1.di.simulejos;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.Serializable;

import javax.media.opengl.awt.GLJPanel;

import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

public class Camera implements Serializable {
	private static final long serialVersionUID = -1189555495876080971L;

	private volatile Vector3 position = new Vector3(0, 0, -10);
	private volatile double angleX;
	private volatile double angleY;

	private static final double ROTATION_DELTA = 0.1;

	private transient volatile GLJPanel canvas;

	private transient final KeyAdapter keyListener = new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				angleX -= ROTATION_DELTA;
				break;
			case KeyEvent.VK_RIGHT:
				angleX += ROTATION_DELTA;
				break;
			case KeyEvent.VK_UP:
				angleY -= ROTATION_DELTA;
				break;
			case KeyEvent.VK_DOWN:
				angleY += ROTATION_DELTA;
				break;
			default:
				return;
			}
			if (canvas != null) {
				canvas.display();
			}
		}
	};

	private transient final MouseAdapter mouseListener = new MouseAdapter() {
		private volatile int x0;
		private volatile int y0;

		@Override
		public void mousePressed(MouseEvent event) {
			x0 = event.getX();
			y0 = event.getY();
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			final int x = event.getX();
			final int y = event.getY();
			angleX += Math.toRadians(x - x0) / 4;
			angleY += Math.toRadians(y - y0) / 4;
			canvas.repaint();
			x0 = x;
			y0 = y;
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			position = position.plus(new Vector3(
					Math.cos(angleY + Math.PI / 2), Math.sin(angleX), Math
							.sin(angleY + Math.PI / 2)));
			canvas.repaint();
		}
	};

	public void setCanvas(GLJPanel canvas) {
		if (this.canvas != null) {
			this.canvas.removeKeyListener(keyListener);
			this.canvas.removeMouseListener(mouseListener);
			this.canvas.removeMouseMotionListener(mouseListener);
		}
		this.canvas = canvas;
		if (canvas != null) {
			canvas.addKeyListener(keyListener);
			canvas.addMouseListener(mouseListener);
			canvas.addMouseMotionListener(mouseListener);
		}
	}

	public void uniform(Program program) {
		program.uniform("Camera.Position", position);
		program.uniform2f("Camera.Angle", (float) angleX, (float) angleY);
	}
}
