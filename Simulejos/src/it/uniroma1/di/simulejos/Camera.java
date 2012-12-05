package it.uniroma1.di.simulejos;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.awt.GLJPanel;

import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector2;
import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

public class Camera {
	private volatile Vector3 position = new Vector3(0, 0, -5);
	private volatile double angleX;
	private volatile double angleY;

	private static final double ROTATION_DELTA = 0.1;
	private static final double MOVING_DELTA = 0.3;

	private volatile GLJPanel canvas;

	Camera(GLJPanel canvas) {
		this.canvas = canvas;
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
		angleX += dx * ROTATION_DELTA;
		angleY += dy * ROTATION_DELTA;
		canvas.repaint();
	}

	public Vector3 unproject(Vector3 v) {
		return Matrix3.createRotation(0, 1, 0, angleX)
				.by(Matrix3.createRotation(1, 0, 0, angleY))
				.by(new Vector3(v.x / v.z, v.y / v.z, 1 / v.z)).plus(position);
	}

	public Vector3 unproject(Vector2 v, double y) {
		final Matrix3 m = Matrix3.createRotation(1, 0, 0, -angleY).by(
				Matrix3.createRotation(0, 1, 0, -angleX));
		final Vector3 w = Matrix3.create(
				new double[] { -1, m.getAt(0, 2), 0, 0, m.getAt(1, 2), 0, 0,
						m.getAt(2, 2), -1 }).by(
				position.minus(m.by(new Vector3(v.x, v.y, 0))).plus(
						new Vector3(0, y, 0)));
		return new Vector3(w.x, y, w.z);
	}
}
