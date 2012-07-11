package it.uniroma1.di.simulejos.util;

import java.io.Serializable;
import java.util.Arrays;

public class DynamicShortArray implements Serializable {
	private static final long serialVersionUID = 2835884553280128351L;

	private int count;
	private short[] array;

	public DynamicShortArray(int initialCapacity) {
		array = new short[initialCapacity];
	}

	public DynamicShortArray() {
		this(100);
	}

	public int length() {
		return count;
	}

	public short get(int index) {
		if (index >= count) {
			throw new IndexOutOfBoundsException("index: " + index
					+ ", length: " + count);
		}
		return array[index];
	}

	public void put(int index, short value) {
		if (index >= count) {
			throw new IndexOutOfBoundsException("index: " + index
					+ ", length: " + count);
		}
		array[index] = value;
	}

	public void get(int srcPos, short[] dest, int destPos, int length) {
		System.arraycopy(array, srcPos, dest, destPos, length);
	}

	public void put(short[] src, int srcPos, int destPos, int length) {
		System.arraycopy(src, srcPos, array, destPos, length);
	}

	public void setLength(int newLength) {
		if (newLength > array.length) {
			array = Arrays.copyOf(array, newLength);
		}
		count = newLength;
	}

	public void append(short value) {
		if (count >= array.length) {
			array = Arrays.copyOf(array, array.length * 2);
		}
		array[count++] = value;
	}

	public short[] trim() {
		return Arrays.copyOf(array, count);
	}
}
