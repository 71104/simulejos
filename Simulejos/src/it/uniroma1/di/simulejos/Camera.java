package it.uniroma1.di.simulejos;

import java.io.Serializable;

import it.uniroma1.di.simulejos.math.Vector3;
import it.uniroma1.di.simulejos.opengl.Program;

public class Camera implements Serializable {
	private static final long serialVersionUID = -1189555495876080971L;

	private Vector3 position = new Vector3(0, 2, -500);
	private Vector3 heading = Vector3.K;

	public void uniform(Program program) {
		program.uniform("Camera.Position", position);
		program.uniform("Camera.Heading", heading);
	}
}
