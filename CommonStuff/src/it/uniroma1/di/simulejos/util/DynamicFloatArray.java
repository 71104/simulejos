package it.uniroma1.di.simulejos.util;

import java.io.Serializable;
import java.util.Arrays;

public class DynamicFloatArray implements Serializable {
	private static final long serialVersionUID = 2835884553280128351L;

	private int count;
	private float[] array;

	public DynamicFloatArray(int initialCapacity) {
		array = new float[initialCapacity];
	}

	public DynamicFloatArray() {
		this(100);
	}

	public int length() {
		return count;
	}

	public float get(int index) {
		if (index >= count) {
			throw new IndexOutOfBoundsException("index: " + index
					+ ", length: " + count);
		}
		return array[index];
	}

	public void put(int index, float value) {
		if (index >= count) {
			throw new IndexOutOfBoundsException("index: " + index
					+ ", length: " + count);
		}
		array[index] = value;
	}

	public void get(int srcPos, float[] dest, int destPos, int length) {
		System.arraycopy(array, srcPos, dest, destPos, length);
	}

	public void put(float[] src, int srcPos, int destPos, int length) {
		if (destPos + length > array.length) {
			array = Arrays.copyOf(array, destPos + length);
		}
		System.arraycopy(src, srcPos, array, destPos, length);
	}

	public void setLength(int newLength) {
		if (newLength > array.length) {
			array = Arrays.copyOf(array, newLength);
		}
		count = newLength;
	}

	public void append(float value) {
		if (count >= array.length) {
			array = Arrays.copyOf(array, array.length * 2);
		}
		array[count++] = value;
	}

	public float[] trim() {
		return Arrays.copyOf(array, count);
	}
}
