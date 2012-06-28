package it.uniroma1.di.simulejos.opengl;

import javax.media.opengl.GL2GL3;

public class VertexShader extends Shader {
	public VertexShader(GL2GL3 gl) {
		super(gl, Type.VERTEX);
	}

	public VertexShader(GL2GL3 gl, String source) {
		super(gl, Type.VERTEX, source);
	}
}
