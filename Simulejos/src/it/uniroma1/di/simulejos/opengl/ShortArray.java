package it.uniroma1.di.simulejos.opengl;

import java.nio.ShortBuffer;

import it.uniroma1.di.simulejos.opengl.Buffer.Usage;

import javax.media.opengl.GL2GL3;

public class ShortArray extends VertexArray {
	public ShortArray(GL2GL3 gl, int index, int components, short[] data) {
		super(gl, index, components, data, false, Usage.STATIC_DRAW);
	}

	public ShortArray(GL2GL3 gl, int index, int components, ShortBuffer data) {
		super(gl, index, components, data, false, Usage.STATIC_DRAW);
	}

	public ShortArray(GL2GL3 gl, int index, int components, short[] data,
			boolean normalize) {
		super(gl, index, components, data, normalize, Usage.STATIC_DRAW);
	}

	public ShortArray(GL2GL3 gl, int index, int components, ShortBuffer data,
			boolean normalize) {
		super(gl, index, components, data, normalize, Usage.STATIC_DRAW);
	}

	public ShortArray(GL2GL3 gl, int index, int components, short[] data,
			boolean normalize, Usage usage) {
		super(gl, index, components, data, normalize, usage);
	}

	public ShortArray(GL2GL3 gl, int index, int components, ShortBuffer data,
			boolean normalize, Usage usage) {
		super(gl, index, components, data, normalize, usage);
	}
}
