package it.uniroma1.di.simulejos.opengl;

import java.io.IOException;
import java.io.Reader;

import javax.media.opengl.GL2GL3;

public class FragmentShader extends Shader {
	public FragmentShader(GL2GL3 gl) {
		super(gl, Type.FRAGMENT);
	}

	public FragmentShader(GL2GL3 gl, String source) {
		super(gl, Type.FRAGMENT, source);
	}

	public FragmentShader(GL2GL3 gl, Reader source) throws IOException {
		super(gl, Type.FRAGMENT, source);
	}

	public FragmentShader(GL2GL3 gl, Class<?> c, String name)
			throws IOException {
		super(gl, Type.FRAGMENT, c, name + ".frag");
	}
}
