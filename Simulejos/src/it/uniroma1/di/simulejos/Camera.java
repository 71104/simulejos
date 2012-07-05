package it.uniroma1.di.simulejos;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.Serializable;

import it.uniroma1.di.simulejos.math.Matrix3;
import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

public class Camera implements Serializable {
	private static final long serialVersionUID = -1189555495876080971L;

	private Vector3 position = new Vector3(0, 0, -10);
	private Matrix3 heading = Matrix3.IDENTITY;

	public final KeyAdapter keyListener = new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent event) {
			// TODO
		}
	};

	public final MouseAdapter mouseListener = new MouseAdapter() {
		@Override
		public void mouseDragged(MouseEvent event) {
			// TODO
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent event) {
			// TODO
		}
	};

	public void uniform(Program program) {
		program.uniform("Camera.Position", position);
		program.uniform("Camera.Heading", heading);
	}
}
