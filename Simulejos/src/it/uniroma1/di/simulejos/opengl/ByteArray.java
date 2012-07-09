package it.uniroma1.di.simulejos.opengl;

import java.nio.ByteBuffer;

import it.uniroma1.di.simulejos.opengl.Buffer.Usage;

import javax.media.opengl.GL2GL3;

public class ByteArray extends VertexArray {
	public ByteArray(GL2GL3 gl, int index, int components, byte[] data) {
		super(gl, index, components, data, false, Usage.STATIC_DRAW);
	}

	public ByteArray(GL2GL3 gl, int index, int components, ByteBuffer data) {
		super(gl, index, components, data, false, Usage.STATIC_DRAW);
	}

	public ByteArray(GL2GL3 gl, int index, int components, byte[] data,
			boolean normalize) {
		super(gl, index, components, data, normalize, Usage.STATIC_DRAW);
	}

	public ByteArray(GL2GL3 gl, int index, int components, ByteBuffer data,
			boolean normalize) {
		super(gl, index, components, data, normalize, Usage.STATIC_DRAW);
	}

	public ByteArray(GL2GL3 gl, int index, int components, byte[] data,
			boolean normalize, Usage usage) {
		super(gl, index, components, data, normalize, usage);
	}

	public ByteArray(GL2GL3 gl, int index, int components, ByteBuffer data,
			boolean normalize, Usage usage) {
		super(gl, index, components, data, normalize, usage);
	}
}
