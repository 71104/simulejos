package it.uniroma1.di.simulejos;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.Serializable;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLJPanel;

import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

public class Camera implements Serializable {
	private static final long serialVersionUID = -1189555495876080971L;

	private volatile Vector3 position = new Vector3(0, 0, -10);
	private volatile Matrix3 heading = Matrix3.IDENTITY;

	private transient volatile GLAutoDrawable canvas;

	private static final double ROTATION_DELTA = 0.01;
	private static final Matrix3 LEFT_ROTATION = Matrix3.createRotation(0, 1,
			0, -ROTATION_DELTA);
	private static final Matrix3 RIGHT_ROTATION = Matrix3.createRotation(0, 1,
			0, ROTATION_DELTA);
	private static final Matrix3 UP_ROTATION = Matrix3.createRotation(1, 0, 0,
			-ROTATION_DELTA);
	private static final Matrix3 DOWN_ROTATION = Matrix3.createRotation(1, 0,
			0, ROTATION_DELTA);

	public transient final KeyAdapter keyListener = new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				heading = LEFT_ROTATION.by(heading);
				break;
			case KeyEvent.VK_RIGHT:
				heading = RIGHT_ROTATION.by(heading);
				break;
			case KeyEvent.VK_UP:
				heading = UP_ROTATION.by(heading);
				break;
			case KeyEvent.VK_DOWN:
				heading = DOWN_ROTATION.by(heading);
				break;
			default:
				return;
			}
			if (canvas != null) {
				canvas.display();
			}
		}
	};

	public transient final MouseAdapter mouseListener = new MouseAdapter() {
		@Override
		public void mouseDragged(MouseEvent event) {
			// TODO
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			// TODO
		}
	};

	public void setCanvas(GLJPanel canvas) {
		this.canvas = canvas;
	}

	public void uniform(Program program) {
		program.uniform("Camera.Position", position);
		program.uniform("Camera.Heading", heading);
	}
}
