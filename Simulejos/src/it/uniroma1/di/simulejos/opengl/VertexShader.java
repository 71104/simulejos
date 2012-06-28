package it.uniroma1.di.simulejos.opengl;

import java.io.IOException;
import java.io.Reader;

import javax.media.opengl.GL2GL3;

public class VertexShader extends Shader {
	public VertexShader(GL2GL3 gl) {
		super(gl, Type.FRAGMENT);
	}

	public VertexShader(GL2GL3 gl, String source) {
		super(gl, Type.FRAGMENT, source);
	}

	public VertexShader(GL2GL3 gl, Reader source) throws IOException {
		super(gl, Type.FRAGMENT, source);
	}

	public VertexShader(GL2GL3 gl, Class<?> c, String name) throws IOException {
		super(gl, Type.FRAGMENT, c, name + ".vert");
	}
}
