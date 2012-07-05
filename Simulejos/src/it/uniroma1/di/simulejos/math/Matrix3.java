package it.uniroma1.di.simulejos.math;

import java.io.Serializable;
import java.util.Arrays;

public class Matrix3 implements Cloneable, Serializable {
	private static final long serialVersionUID = 4389630396481063939L;

	public static final Matrix3 NULL = new Matrix3(new double[] { 0, 0, 0, 0,
			0, 0, 0, 0, 0 });
	public static final Matrix3 IDENTITY = new Matrix3(new double[] { 1, 0, 0,
			0, 1, 0, 0, 0, 1 });

	private final double values[] = new double[9];

	public Matrix3(double[] values) {
		if (values.length != 9) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(values, 0, this.values, 0, 9);
	}

	public Matrix3(double[][] values) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.values[i * 3 + j] = values[i][j];
			}
		}
	}

	@Override
	public String toString() {
		return "((" + values[0] + ", " + values[1] + ", " + values[2] + "), ("
				+ values[3] + ", " + values[4] + ", " + values[5] + "), ("
				+ values[6] + ", " + values[7] + ", " + values[8] + "))";
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
		Matrix3 other = (Matrix3) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	@Override
	public Matrix3 clone() {
		return new Matrix3(values);
	}

	public double[] toArray() {
		return values.clone();
	}

	public double getAt(int i, int j) {
		return values[i * 3 + j];
	}

	public double determinant() {
		return values[0] * (values[4] * values[8] - values[5] * values[7])
				- values[1] * (values[3] * values[8] - values[5] * values[6])
				+ values[2] * (values[3] * values[7] - values[4] * values[6]);
	}

	public Matrix3 by(double a) {
		return new Matrix3(new double[][] {
				{ values[0] * a, values[1] * a, values[2] * a },
				{ values[3] * a, values[4] * a, values[5] * a },
				{ values[6] * a, values[7] * a, values[8] * a } });
	}

	public Vector3 by(Vector3 v) {
		return new Vector3(values[0] * v.x + values[1] * v.y + values[2] * v.z,
				values[3] * v.x + values[4] * v.y + values[5] * v.z, values[6]
						* v.x + values[7] * v.y + values[8] * v.z);
	}
}
