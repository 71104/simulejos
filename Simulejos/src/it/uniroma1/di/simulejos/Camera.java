package it.uniroma1.di.simulejos;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.media.opengl.awt.GLJPanel;

import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

public class Camera implements Serializable {
	private static final long serialVersionUID = -1189555495876080971L;

	private volatile Vector3 position = new Vector3(0, 0, -5);
	private volatile double angleX;
	private volatile double angleY;

	private static final double ROTATION_DELTA = 0.1;
	private static final double MOVING_DELTA = 1;

	private transient volatile GLJPanel canvas;

	private transient final KeyAdapter keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				angleX += ROTATION_DELTA;
				break;
			case KeyEvent.VK_RIGHT:
				angleX -= ROTATION_DELTA;
				break;
			case KeyEvent.VK_UP:
				angleY += ROTATION_DELTA;
				break;
			case KeyEvent.VK_DOWN:
				angleY -= ROTATION_DELTA;
				break;
			case KeyEvent.VK_W:
				position = position.plus(new Vector3(MOVING_DELTA
						* Math.cos(angleX + Math.PI / 2), MOVING_DELTA
						* Math.sin(angleY), MOVING_DELTA
						* Math.sin(angleX + Math.PI / 2)));
				break;
			case KeyEvent.VK_S:
				position = position.minus(new Vector3(MOVING_DELTA
						* Math.cos(angleX + Math.PI / 2), MOVING_DELTA
						* Math.sin(angleY), MOVING_DELTA
						* Math.sin(angleX + Math.PI / 2)));
				break;
			case KeyEvent.VK_A:
				position = position
						.minus(new Vector3(MOVING_DELTA * Math.sin(angleX), 0,
								MOVING_DELTA * -Math.cos(angleX)));
				break;
			case KeyEvent.VK_D:
				position = position
						.plus(new Vector3(MOVING_DELTA * Math.cos(angleX), 0,
								MOVING_DELTA * Math.sin(angleX)));
				break;
			default:
				return;
			}
			if (canvas != null) {
				canvas.repaint();
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
