package it.uniroma1.di.simulejos.opengl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2GL3;

public class Arrays {
	private volatile int nextIndex;
	private final GL2GL3 gl;
	private final List<VertexArray> arrays = Collections
			.synchronizedList(new LinkedList<VertexArray>());

	public Arrays(GL2GL3 gl) {
		this.gl = gl;
	}

	public void add(int components, byte[] data) {
		arrays.add(new ByteArray(gl, nextIndex++, components, data));
	}

	public void add(int components, short[] data) {
		arrays.add(new ShortArray(gl, nextIndex++, components, data));
	}

	public void add(int components, int[] data) {
		arrays.add(new IntArray(gl, nextIndex++, components, data));
	}

	public void add(int components, float[] data) {
		arrays.add(new FloatArray(gl, nextIndex++, components, data));
	}

	public void add(int components, double[] data) {
		arrays.add(new DoubleArray(gl, nextIndex++, components, data));
	}

	public void bind() {
		for (VertexArray array : arrays) {
			array.bind();
		}
	}

	public void draw(int mode, int first, int count) {
		for (VertexArray array : arrays) {
			array.bind();
		}
		gl.glDrawArrays(mode, first, count);
	}

	public void delete() {
		for (VertexArray array : arrays) {
			array.delete();
		}
	}
}
