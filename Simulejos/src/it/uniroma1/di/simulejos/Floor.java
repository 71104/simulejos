package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.opengl.Program;

import java.io.IOException;
import java.io.Serializable;

import javax.media.opengl.GL2GL3;

public final class Floor implements Serializable {
	private static final long serialVersionUID = -6429459719709791948L;

	private transient volatile Program program;

	void setGL(GL2GL3 gl) {
		try {
			program = new Program(gl, Floor.class, "floor");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void draw(GL2GL3 gl) {
		program.use();
		// TODO
	}
}
