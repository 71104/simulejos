package it.uniroma1.di.simulejos.opengl;

import it.uniroma1.di.simulejos.opengl.Buffer.Target;
import it.uniroma1.di.simulejos.opengl.Buffer.Usage;

import javax.media.opengl.GL2GL3;

public class ShortArray {
	private final Buffer buffer;

	public ShortArray(GL2GL3 gl, int index, int components, short[] data) {
		this.buffer = new Buffer(gl, Target.ARRAY, Usage.STATIC_DRAW);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_SHORT, false, 0,
				0);
	}

	public ShortArray(GL2GL3 gl, int index, int components, short[] data,
			boolean normalize) {
		this.buffer = new Buffer(gl, Target.ARRAY, Usage.STATIC_DRAW);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_SHORT, normalize,
				0, 0);
	}

	public ShortArray(GL2GL3 gl, int index, int components, short[] data,
			boolean normalize, Usage usage) {
		this.buffer = new Buffer(gl, Target.ARRAY, usage);
		this.buffer.bind();
		this.buffer.data(data);
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(index, components, GL2GL3.GL_SHORT, normalize,
				0, 0);
	}

	public void bind() {
		buffer.bind();
	}

	public void delete() {
		buffer.delete();
	}
}
