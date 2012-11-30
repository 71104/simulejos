package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.opengl.Arrays;
import it.uniroma1.di.simulejos.opengl.Program;
import it.uniroma1.di.simulejos.opengl.Texture2D;

import java.awt.image.BufferedImage;
import javax.media.opengl.GL2GL3;

import static javax.media.opengl.GL2GL3.*;

public final class Floor {
	private volatile BufferedImage textureImage;
	private volatile float width;
	private volatile float depth;
	private volatile boolean repeatX;
	private volatile boolean repeatY;
	private transient volatile boolean updateTexture;

	private transient volatile Program program;
	private transient volatile Arrays arrays;
	private transient volatile Texture2D texture;

	public boolean isRepeatX() {
		return repeatX;
	}

	public boolean isRepeatY() {
		return repeatY;
	}

	public void configure(BufferedImage textureImage, float width, float depth,
			boolean repeatX, boolean repeatY) {
		this.textureImage = textureImage;
		this.width = width;
		this.depth = depth;
		this.repeatX = repeatX;
		this.repeatY = repeatY;
		this.updateTexture = true;
	}

	private void updateTexture(GL2GL3 gl) {
		if (textureImage != null) {
			texture = new Texture2D(gl, textureImage);
			texture.parameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			if (!repeatX) {
				texture.parameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
			}
			if (!repeatY) {
				texture.parameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
			}
			if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
				final float[] anisotropy = new float[1];
				gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy, 0);
				texture.parameter(GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy[0]);
			}
		} else {
			texture = null;
		}
	}

	void init(GL2GL3 gl) {
		program = new Program(gl, getClass(), "floor",
				new String[] { "in_Vertex" });
		arrays = new Arrays(gl, 6);
		arrays.add(4, new double[] { 0, 0, 0, 1, 1, 0, 1, 0, -1, 0, 1, 0, -1,
				0, -1, 0, 1, 0, -1, 0, 1, 0, 1, 0 });
		updateTexture(gl);
	}

	void share(GL2GL3 gl) {
		arrays.share(gl);
	}

	void draw(GL2GL3 gl, Camera camera) {
		if (updateTexture) {
			updateTexture(gl);
		}
		updateTexture = false;
		program.use();
		camera.uniform(program);
		if (texture != null) {
			program.uniform("UseTexture", true);
			program.uniform2f("Size", width, depth);
			texture.bind();
		} else {
			program.uniform("UseTexture", false);
		}
		arrays.bindAndDraw(GL_TRIANGLE_FAN);
	}

	void drawForSensor(GL2GL3 gl, Program program) {
		if (texture != null) {
			program.uniform(gl, "UseTexture", true);
			program.uniform2f("Size", width, depth);
			texture.bind(gl);
		} else {
			program.uniform(gl, "UseTexture", false);
		}
		arrays.bindAndDraw(gl, GL_TRIANGLE_FAN);
	}
}
