package it.uniroma1.di.simulejos.opengl;

import java.nio.ShortBuffer;

import javax.media.opengl.GL2GL3;

public class ElementArrayBuffer extends Buffer {
	public ElementArrayBuffer(GL2GL3 gl, short[] indices) {
		super(gl, Target.ELEMENT_ARRAY, Usage.STATIC_DRAW);
		data(indices);
	}

	public ElementArrayBuffer(GL2GL3 gl, ShortBuffer indices) {
		super(gl, Target.ELEMENT_ARRAY, Usage.STATIC_DRAW);
		data(indices.limit() * 2, indices);
	}

	public ElementArrayBuffer(GL2GL3 gl, short[] indices, Usage usage) {
		super(gl, Target.ELEMENT_ARRAY, usage);
		data(indices);
	}

	public ElementArrayBuffer(GL2GL3 gl, ShortBuffer indices, Usage usage) {
		super(gl, Target.ELEMENT_ARRAY, usage);
		data(indices.limit() * 2, indices);
	}
}
