package it.uniroma1.di.simulejos;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.awt.GLJPanel;

import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

public class Camera {
	private volatile Vector3 position = new Vector3(0, 0, -5);
	private volatile double angleX;
	private volatile double angleY;

	private static final double ROTATION_DELTA = 0.1;
	private static final double MOVING_DELTA = 0.3;

	private volatile GLJPanel canvas;

	private final KeyAdapter keyListener = new KeyAdapter() {
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
				move(0, 1);
				break;
			case KeyEvent.VK_S:
				move(0, -1);
				break;
			case KeyEvent.VK_A:
				move(-1, 0);
				break;
			case KeyEvent.VK_D:
				move(1, 0);
				break;
			default:
				return;
			}
			canvas.repaint();
		}
	};

	Camera(GLJPanel canvas) {
		this.canvas = canvas;
		canvas.addKeyListener(keyListener);
	}

	public void uniform(Program program) {
		program.uniform("Camera.Position", position);
		program.uniform2f("Camera.Angle", (float) angleX, (float) angleY);
	}

	public void uniform(GL2GL3 gl, Program program) {
		program.uniform(gl, "Camera.Position", position);
		program.uniform2f(gl, "Camera.Angle", (float) angleX, (float) angleY);
	}

	public void move(double dx, double dz) {
		position = position.plus(new Vector3(dx * Math.cos(angleX) + dz
				* Math.cos(angleX + Math.PI / 2), dz * Math.sin(angleY), dx
				* Math.sin(angleX) + dz * Math.sin(angleX + Math.PI / 2))
				.by(MOVING_DELTA));
		canvas.repaint();
	}

	public void rotate(double dx, double dy) {
		angleX += dx;
		angleY += dy;
		canvas.repaint();
	}
}
