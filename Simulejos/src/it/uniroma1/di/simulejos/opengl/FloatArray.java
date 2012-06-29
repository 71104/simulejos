package it.uniroma1.di.simulejos.opengl;

import it.uniroma1.di.simulejos.opengl.Buffer.Usage;

import javax.media.opengl.GL2GL3;

public class FloatArray extends VertexArray {
	public FloatArray(GL2GL3 gl, int index, int components, float[] data) {
		super(gl, index, components, data, false, Usage.STATIC_DRAW);
	}

	public FloatArray(GL2GL3 gl, int index, int components, float[] data,
			boolean normalize) {
		super(gl, index, components, data, normalize, Usage.STATIC_DRAW);
	}

	public FloatArray(GL2GL3 gl, int index, int components, float[] data,
			boolean normalize, Usage usage) {
		super(gl, index, components, data, normalize, usage);
	}
}
