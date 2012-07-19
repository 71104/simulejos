package it.uniroma1.di.simulejos.opengl;

import javax.media.opengl.GL2GL3;
import static javax.media.opengl.GL2GL3.*;

public class Texture2D extends GLObject {
	private static int create(GL2GL3 gl) {
		final int[] result = new int[1];
		gl.glGenTextures(1, result, 0);
		return result[0];
	}

	public Texture2D(GL2GL3 gl) {
		super(gl, create(gl));
	}

	public void bind() {
		gl.glBindTexture(GL_TEXTURE_2D, id);
	}

	public void image(int level, int internalFormat, int width, int height,
			int border, int format, int type, java.nio.Buffer data) {
		gl.glTexImage2D(GL_TEXTURE_2D, level, internalFormat, width, height,
				border, format, type, data);
	}

	public void image(int level, int internalFormat, int width, int height,
			int border, int format, int type, long data) {
		gl.glTexImage2D(GL_TEXTURE_2D, level, internalFormat, width, height,
				border, format, type, data);
	}

	public void delete() {
		gl.glDeleteTextures(1, new int[] { id }, 0);
	}
}
