package it.uniroma1.di.simulejos.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2GL3;

public class Elements {
	private volatile int nextIndex;
	private final GL2GL3 gl;
	private final int count;
	private final ElementArrayBuffer elementArray;
	private final List<VertexArray> arrays = Collections
			.synchronizedList(new LinkedList<VertexArray>());

	public Elements(GL2GL3 gl, short[] indices) {
		this.gl = gl;
		this.count = indices.length;
		this.elementArray = new ElementArrayBuffer(gl, indices);
	}

	public Elements(GL2GL3 gl, ShortBuffer indices) {
		this.gl = gl;
		this.count = indices.limit();
		this.elementArray = new ElementArrayBuffer(gl, indices);
	}

	public void add(int components, byte[] data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new ByteArray(gl, nextIndex++, components, data));
	}

	public void add(int components, ByteBuffer data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new ByteArray(gl, nextIndex++, components, data));
	}

	public void add(int components, short[] data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new ShortArray(gl, nextIndex++, components, data));
	}

	public void add(int components, ShortBuffer data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new ShortArray(gl, nextIndex++, components, data));
	}

	public void add(int components, int[] data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new IntArray(gl, nextIndex++, components, data));
	}

	public void add(int components, IntBuffer data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new IntArray(gl, nextIndex++, components, data));
	}

	public void add(int components, float[] data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new FloatArray(gl, nextIndex++, components, data));
	}

	public void add(int components, FloatBuffer data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new FloatArray(gl, nextIndex++, components, data));
	}

	public void add(int components, double[] data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new DoubleArray(gl, nextIndex++, components, data));
	}

	public void add(int components, DoubleBuffer data) {
		gl.glEnableVertexAttribArray(nextIndex);
		arrays.add(new DoubleArray(gl, nextIndex++, components, data));
	}

	public void bind() {
		elementArray.bind();
		for (VertexArray array : arrays) {
			array.bind();
		}
	}

	public void draw(int mode) {
		gl.glDrawElements(mode, count, GL2GL3.GL_UNSIGNED_SHORT, 0);
	}

	public void bindAndDraw(int mode) {
		elementArray.bind();
		for (VertexArray array : arrays) {
			array.bind();
		}
		gl.glDrawElements(mode, count, GL2GL3.GL_UNSIGNED_SHORT, 0);
	}

	public void delete() {
		elementArray.delete();
		for (VertexArray array : arrays) {
			array.delete();
		}
	}
}
