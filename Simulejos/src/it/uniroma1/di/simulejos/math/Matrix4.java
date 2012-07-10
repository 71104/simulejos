package it.uniroma1.di.simulejos.math;

import java.io.Serializable;
import java.util.Arrays;

public class Matrix4 implements Cloneable, Serializable {
	private static final long serialVersionUID = -3293211504653809206L;

	public static final Matrix4 NULL = new Matrix4(new double[] { 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
	public static final Matrix4 IDENTITY = new Matrix4(new double[] { 1, 0, 0,
			0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 });

	private final double[] values;

	private Matrix4(double[] values) {
		if (values.length != 16) {
			throw new IllegalArgumentException();
		}
		this.values = values;
	}

	public static Matrix4 create(double[] values) {
		return new Matrix4(values.clone());
	}

	public static Matrix4 create(double[][] values) {
		final double[] array = new double[16];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				array[i * 4 + j] = values[i][j];
			}
		}
		return new Matrix4(array);
	}

	@Override
	public String toString() {
		return "((" + values[0] + ", " + values[1] + ", " + values[2] + "), ("
				+ values[3] + ", " + values[4] + ", " + values[5] + "), ("
				+ values[6] + ", " + values[7] + ", " + values[8] + ", "
				+ values[9] + ", " + values[10] + ", " + values[11] + ", "
				+ values[12] + ", " + values[13] + ", " + values[14] + ", "
				+ values[15] + "))";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Matrix4 other = (Matrix4) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	@Override
	public Matrix4 clone() {
		return new Matrix4(values.clone());
	}

	public double[] toArray() {
		return values.clone();
	}

	public double getAt(int i, int j) {
		return values[i * 4 + j];
	}

	// TODO
}
