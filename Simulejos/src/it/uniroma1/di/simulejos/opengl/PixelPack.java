package it.uniroma1.di.simulejos.opengl;

import javax.media.opengl.GL2GL3;

public class PixelPack extends Buffer {
	public PixelPack(GL2GL3 gl) {
		super(gl, Target.PIXEL_PACK);
	}
}
