package it.uniroma1.di.simulejos.opengl;

import java.nio.DoubleBuffer;

import it.uniroma1.di.simulejos.opengl.Buffer.Usage;

import javax.media.opengl.GL2GL3;

public class DoubleArray extends VertexArray {
	public DoubleArray(GL2GL3 gl, int index, int components, double[] data) {
		super(gl, index, components, data, false, Usage.STATIC_DRAW);
	}

	public DoubleArray(GL2GL3 gl, int index, int components, DoubleBuffer data) {
		super(gl, index, components, data, false, Usage.STATIC_DRAW);
	}

	public DoubleArray(GL2GL3 gl, int index, int components, double[] data,
			boolean normalize) {
		super(gl, index, components, data, normalize, Usage.STATIC_DRAW);
	}

	public DoubleArray(GL2GL3 gl, int index, int components, DoubleBuffer data,
			boolean normalize) {
		super(gl, index, components, data, normalize, Usage.STATIC_DRAW);
	}

	public DoubleArray(GL2GL3 gl, int index, int components, double[] data,
			boolean normalize, Usage usage) {
		super(gl, index, components, data, normalize, usage);
	}

	public DoubleArray(GL2GL3 gl, int index, int components, DoubleBuffer data,
			boolean normalize, Usage usage) {
		super(gl, index, components, data, normalize, usage);
	}
}
