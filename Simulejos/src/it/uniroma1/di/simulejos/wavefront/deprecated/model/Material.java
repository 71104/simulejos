package it.uniroma1.di.simulejos.wavefront.deprecated.model;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import static javax.media.opengl.GL.*;

public final class Material {
	public final String name;
	public Color ambient;
	public Color diffuse;
	public Color specular;

	private boolean blend;
	private float alpha = 1.0f;

	private BufferedImage texture;
	private int textureName;

	public Material(String name) {
		this.name = name;
	}

	public void setAlpha(float alpha) {
		blend = true;
		this.alpha = alpha;
	}

	public void setTexture(BufferedImage texture, GL gl) {
		if (this.texture != null) {
			throw new IllegalStateException();
		}
		this.texture = texture;

		int textureNames[] = new int[1];
		gl.glGenTextures(1, textureNames, 0);
		textureName = textureNames[0];
		gl.glBindTexture(GL_TEXTURE_2D, textureName);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		int width = texture.getWidth();
		int height = texture.getHeight();
		int data[] = texture.getRGB(0, 0, width, height, null, 0, width);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, 3, width, height, 0, GL_BGRA,
				GL_UNSIGNED_BYTE, IntBuffer.wrap(data));

		gl.glBindTexture(GL_TEXTURE_2D, 0);
	}
}
