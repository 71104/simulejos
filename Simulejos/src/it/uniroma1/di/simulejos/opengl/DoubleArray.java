package it.uniroma1.di.simulejos.opengl;

import it.uniroma1.di.simulejos.opengl.Buffer.Target;
import it.uniroma1.di.simulejos.opengl.Buffer.Usage;

import javax.media.opengl.GL2GL3;

public class DoubleArray {
	private final Buffer buffer;

	public DoubleArray(GL2GL3 gl, int index, int components, double[] data) {
		this.buffer = new Buffer(gl, Target.ARRAY, Usage.STATIC_DRAW);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_DOUBLE, false, 0,
				0);
	}

	public DoubleArray(GL2GL3 gl, int index, int components, double[] data,
			boolean normalize) {
		this.buffer = new Buffer(gl, Target.ARRAY, Usage.STATIC_DRAW);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_DOUBLE,
				normalize, 0, 0);
	}

	public DoubleArray(GL2GL3 gl, int index, int components, double[] data,
			boolean normalize, Usage usage) {
		this.buffer = new Buffer(gl, Target.ARRAY, usage);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_DOUBLE,
				normalize, 0, 0);
	}

	public void bind() {
		buffer.bind();
	}

	public void delete() {
		buffer.delete();
	}
}
