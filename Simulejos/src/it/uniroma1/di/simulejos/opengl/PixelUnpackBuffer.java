package it.uniroma1.di.simulejos.opengl;

import javax.media.opengl.GL2GL3;

public class PixelUnpackBuffer extends Buffer {
	public PixelUnpackBuffer(GL2GL3 gl, Usage usage) {
		super(gl, Target.PIXEL_UNPACK, usage);
	}
}
