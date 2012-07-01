package it.uniroma1.di.simulejos.opengl;

import java.nio.IntBuffer;

import it.uniroma1.di.simulejos.opengl.Buffer.Usage;

import javax.media.opengl.GL2GL3;

public class IntArray extends VertexArray {
	public IntArray(GL2GL3 gl, int index, int components, int[] data) {
		super(gl, index, components, data, false, Usage.STATIC_DRAW);
	}

	public IntArray(GL2GL3 gl, int index, int components, IntBuffer data) {
		super(gl, index, components, data, false, Usage.STATIC_DRAW);
	}

	public IntArray(GL2GL3 gl, int index, int components, int[] data,
			boolean normalize) {
		super(gl, index, components, data, normalize, Usage.STATIC_DRAW);
	}

	public IntArray(GL2GL3 gl, int index, int components, IntBuffer data,
			boolean normalize) {
		super(gl, index, components, data, normalize, Usage.STATIC_DRAW);
	}

	public IntArray(GL2GL3 gl, int index, int components, int[] data,
			boolean normalize, Usage usage) {
		super(gl, index, components, data, normalize, usage);
	}

	public IntArray(GL2GL3 gl, int index, int components, IntBuffer data,
			boolean normalize, Usage usage) {
		super(gl, index, components, data, normalize, usage);
	}
}
