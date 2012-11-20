package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.opengl.Arrays;
import it.uniroma1.di.simulejos.opengl.Program;
import it.uniroma1.di.simulejos.opengl.Texture2D;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2GL3;

import static javax.media.opengl.GL2GL3.*;

public final class Floor implements Externalizable {
	private volatile BufferedImage textureImage;
	private volatile boolean repeatX;
	private volatile boolean repeatY;
	private transient volatile boolean updateTexture;

	private transient volatile Program program;
	private transient volatile Arrays arrays;
	private transient volatile Texture2D texture;

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		final byte[] textureBytes = (byte[]) in.readObject();
		if (textureBytes != null) {
			textureImage = ImageIO.read(new ByteArrayInputStream(textureBytes));
		} else {
			textureImage = null;
		}
		repeatX = in.readBoolean();
		repeatY = in.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if (textureImage != null) {
			final ByteArrayOutputStream byteSink = new ByteArrayOutputStream();
			ImageIO.write(textureImage, "PNG", byteSink);
			out.writeObject(byteSink.toByteArray());
		} else {
			out.writeObject(null);
		}
		out.writeBoolean(repeatX);
		out.writeBoolean(repeatY);
	}

	public boolean isRepeatX() {
		return repeatX;
	}

	public boolean isRepeatY() {
		return repeatY;
	}

	public void configure(BufferedImage textureImage, boolean repeatX,
			boolean repeatY) {
		this.textureImage = textureImage;
		this.repeatX = repeatX;
		this.repeatY = repeatY;
		this.updateTexture = true;
	}

	private void updateTexture(GL2GL3 gl) {
		if (textureImage != null) {
			texture = new Texture2D(gl, textureImage);
			if (!repeatX) {
				texture.parameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
			}
			if (!repeatY) {
				texture.parameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
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
			program.uniform1i("UseTexture", GL_TRUE);
			texture.bind();
		} else {
			program.uniform1i("UseTexture", GL_FALSE);
		}
		arrays.bindAndDraw(GL_TRIANGLE_FAN);
	}

	void drawForSensor(GL2GL3 gl, Program program) {
		if (texture != null) {
			program.uniform1i(gl, "UseTexture", GL_TRUE);
			texture.bind(gl);
		} else {
			program.uniform1i(gl, "UseTexture", GL_FALSE);
		}
		arrays.bindAndDraw(gl, GL_TRIANGLE_FAN);
	}
}
