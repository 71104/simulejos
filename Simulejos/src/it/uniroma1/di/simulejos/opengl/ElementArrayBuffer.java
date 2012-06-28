package it.uniroma1.di.simulejos.opengl;

import javax.media.opengl.GL2GL3;

public class ElementArrayBuffer extends Buffer {
	public ElementArrayBuffer(GL2GL3 gl, Usage usage) {
		super(gl, Target.ELEMENT_ARRAY, usage);
	}
}
