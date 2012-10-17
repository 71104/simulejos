package it.uniroma1.di.simulejos.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2GL3;

import it.uniroma1.di.simulejos.opengl.Buffer.Usage;
import static javax.media.opengl.GL2GL3.*;

public class VertexArray extends GLObject {
	public final int count;
	private final ArrayBuffer buffer;

	private final int components;
	private final int type;
	private final boolean normalize;

	public static class AlignmentException extends RuntimeException {
		private static final long serialVersionUID = -2140957103681383648L;

		AlignmentException(int length, int components) {
			super(
					"Misaligned vertex array. Data length: "
							+ length
							+ ", not a multiple of the specified number of components: "
							+ components);
		}
	}

	protected VertexArray(GL2GL3 gl, int index, int components, byte[] data,
			boolean normalize, Usage usage) {
		super(gl, index);
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_BYTE;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_BYTE, normalize, 0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components,
			ByteBuffer data, boolean normalize, Usage usage) {
		super(gl, index);
		final int count = data.limit();
		if (count % components != 0) {
			throw new AlignmentException(count, components);
		}
		this.count = count / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_BYTE;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_BYTE, normalize, 0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components, short[] data,
			boolean normalize, Usage usage) {
		super(gl, index);
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_SHORT;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_SHORT, normalize, 0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components,
			ShortBuffer data, boolean normalize, Usage usage) {
		super(gl, index);
		final int count = data.limit();
		if (count % components != 0) {
			throw new AlignmentException(count, components);
		}
		this.count = count / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_SHORT;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_SHORT, normalize, 0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components, int[] data,
			boolean normalize, Usage usage) {
		super(gl, index);
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_INT;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_INT, normalize, 0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components, IntBuffer data,
			boolean normalize, Usage usage) {
		super(gl, index);
		final int count = data.limit();
		if (count % components != 0) {
			throw new AlignmentException(count, components);
		}
		this.count = count / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_INT;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_INT, normalize, 0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components, float[] data,
			boolean normalize, Usage usage) {
		super(gl, index);
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_FLOAT;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_FLOAT, normalize, 0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components,
			FloatBuffer data, boolean normalize, Usage usage) {
		super(gl, index);
		final int count = data.limit();
		if (count % components != 0) {
			throw new AlignmentException(count, components);
		}
		this.count = count / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_FLOAT;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_FLOAT, normalize, 0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components, double[] data,
			boolean normalize, Usage usage) {
		super(gl, index);
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_DOUBLE;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_DOUBLE, normalize, 0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components,
			DoubleBuffer data, boolean normalize, Usage usage) {
		super(gl, index);
		final int count = data.limit();
		if (count % components != 0) {
			throw new AlignmentException(count, components);
		}
		this.count = count / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.data(data);
		this.components = components;
		this.type = GL_DOUBLE;
		this.normalize = normalize;
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL_DOUBLE, normalize, 0, 0);
	}

	public final void bind() {
		buffer.bind();
		gl.glVertexAttribPointer(id, components, type, normalize, 0, 0);
	}

	public final void delete() {
		buffer.delete();
	}
}
