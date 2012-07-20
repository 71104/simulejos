package it.uniroma1.di.simulejos.opengl;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

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
		bind();
	}

	public Texture2D(GL2GL3 gl, BufferedImage image) {
		super(gl, create(gl));
		bind();
		setMinFilter(GL_LINEAR);
		setMagFilter(GL_LINEAR);
		// TODO
	}

	public void bind() {
		gl.glBindTexture(GL_TEXTURE_2D, id);
	}

	public int getParameteri(int name) {
		final int[] values = new int[1];
		gl.glGetTexParameteriv(GL_TEXTURE_2D, name, values, 0);
		return values[0];
	}

	public void getParameteriv(int name, int[] values) {
		gl.glGetTexParameteriv(GL_TEXTURE_2D, name, values, 0);
	}

	public float getParameterf(int name) {
		final float[] values = new float[1];
		gl.glGetTexParameterfv(GL_TEXTURE_2D, name, values, 0);
		return values[0];
	}

	public void getParameterfv(int name, float[] values) {
		gl.glGetTexParameterfv(GL_TEXTURE_2D, name, values, 0);
	}

	public void parameter(int name, float value) {
		gl.glTexParameterf(GL_TEXTURE_2D, name, value);
	}

	public void parameter(int name, int value) {
		gl.glTexParameteri(GL_TEXTURE_2D, name, value);
	}

	public void parameter(int name, float[] values) {
		gl.glTexParameterfv(GL_TEXTURE_2D, name, values, 0);
	}

	public void parameter(int name, int[] values) {
		gl.glTexParameteriv(GL_TEXTURE_2D, name, values, 0);
	}

	public int getMinFilter() {
		return getParameteri(GL_TEXTURE_MIN_FILTER);
	}

	public void setMinFilter(int filterType) {
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterType);
	}

	public int getMagFilter() {
		return getParameteri(GL_TEXTURE_MAG_FILTER);
	}

	public void setMagFilter(int filterType) {
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterType);
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

	public void image(int level, BufferedImage image) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		final int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
		// TODO
	}

	public void delete() {
		gl.glDeleteTextures(1, new int[] { id }, 0);
	}
}
