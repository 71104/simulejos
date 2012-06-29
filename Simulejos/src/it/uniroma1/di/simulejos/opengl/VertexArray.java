package it.uniroma1.di.simulejos.opengl;

import javax.media.opengl.GL2GL3;

import it.uniroma1.di.simulejos.opengl.Buffer.Usage;

public class VertexArray {
	public final int count;
	private final ArrayBuffer buffer;

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
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_BYTE, normalize,
				0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components, short[] data,
			boolean normalize, Usage usage) {
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_SHORT, normalize,
				0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components, int[] data,
			boolean normalize, Usage usage) {
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_INT, normalize,
				0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components, float[] data,
			boolean normalize, Usage usage) {
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_FLOAT, normalize,
				0, 0);
	}

	protected VertexArray(GL2GL3 gl, int index, int components, double[] data,
			boolean normalize, Usage usage) {
		if (data.length % components != 0) {
			throw new AlignmentException(data.length, components);
		}
		this.count = data.length / components;
		this.buffer = new ArrayBuffer(gl, Usage.STATIC_DRAW);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_DOUBLE,
				normalize, 0, 0);
	}

	public final void bind() {
		buffer.bind();
	}

	public final void delete() {
		buffer.delete();
	}
}
