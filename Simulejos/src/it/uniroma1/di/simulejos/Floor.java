package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.opengl.Arrays;
import it.uniroma1.di.simulejos.opengl.Program;

import java.awt.Image;
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
	private volatile BufferedImage texture;
	private volatile boolean repeatX;
	private volatile boolean repeatY;
	private transient volatile boolean updateTexture;

	private transient volatile GL2GL3 gl;
	private transient volatile Program program;
	private transient volatile Arrays arrays;

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		final byte[] textureBytes = (byte[]) in.readObject();
		if (textureBytes != null) {
			texture = ImageIO.read(new ByteArrayInputStream(textureBytes));
		} else {
			texture = null;
		}
		repeatX = in.readBoolean();
		repeatY = in.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if (texture != null) {
			final ByteArrayOutputStream byteSink = new ByteArrayOutputStream();
			ImageIO.write(texture, "PNG", byteSink);
			out.writeObject(byteSink.toByteArray());
		} else {
			out.writeObject(null);
		}
		out.writeBoolean(repeatX);
		out.writeBoolean(repeatY);
	}

	public void configure(Image texture, boolean repeatX, boolean repeatY) {
		// TODO
		this.updateTexture = true;
	}

	void draw(GL2GL3 gl, Camera camera) {
		if (gl != this.gl) {
			// TODO update texture
			program = new Program(gl, getClass(), "floor",
					new String[] { "in_Vertex" });
			arrays = new Arrays(gl, 6);
			arrays.add(4, new double[] { 0, 0, 0, 1, 1, 0, 1, 0, -1, 0, 1, 0,
					-1, 0, -1, 0, 1, 0, -1, 0, 1, 0, 1, 0 });
		} else if (updateTexture) {
			// TODO update texture
		}
		updateTexture = false;
		program.use();
		camera.uniform(program);
		arrays.bindAndDraw(GL_TRIANGLE_FAN);
	}
}
