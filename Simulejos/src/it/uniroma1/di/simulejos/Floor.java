package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.opengl.Arrays;
import it.uniroma1.di.simulejos.opengl.Program;

import java.io.Serializable;

import javax.media.opengl.GL2GL3;

public final class Floor implements Serializable {
	private static final long serialVersionUID = -6429459719709791948L;

	private transient volatile Program program;
	private transient volatile Arrays arrays;

	void setGL(GL2GL3 gl) {
		program = new Program(gl, getClass(), "floor",
				new String[] { "in_Vertex" });
		arrays = new Arrays(gl, 6);
		arrays.add(4, new double[] { 0, 0, 0, 1, -1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
				-1, 0, -1, 0, -1, 0, -1, 0, 1, 0 });
	}

	void draw(GL2GL3 gl) {
		program.use();
		arrays.bindAndDraw(GL2GL3.GL_TRIANGLE_FAN);
	}
}
