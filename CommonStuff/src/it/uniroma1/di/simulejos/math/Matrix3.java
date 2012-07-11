package it.uniroma1.di.simulejos.math;

import java.io.Serializable;
import java.util.Arrays;

public class Matrix3 implements Cloneable, Serializable {
	private static final long serialVersionUID = 4389630396481063939L;

	public static final Matrix3 NULL = new Matrix3(new double[] { 0, 0, 0, 0,
			0, 0, 0, 0, 0 });
	public static final Matrix3 IDENTITY = new Matrix3(new double[] { 1, 0, 0,
			0, 1, 0, 0, 0, 1 });

	private final double values[];

	private Matrix3(double[] values) {
		if (values.length != 9) {
			throw new IllegalArgumentException();
		}
		this.values = values;
	}

	public static Matrix3 create(double[] values) {
		return new Matrix3(values.clone());
	}

	public static Matrix3 createRotation(double x, double y, double z, double a) {
		final double s = Math.sin(a);
		final double c = Math.cos(a);
		return new Matrix3(new double[] { c + x * x * (1 - c),
				x * y * (1 - c) - z * s, x * z * (1 - c) + y * s,
				y * x * (1 - c) + z * s, c + y * y * (1 - c),
				y * z * (1 - c) - x * s, z * x * (1 - c) - y * s,
				z * y * (1 - c) + x * s, c + z * z * (1 - c) });
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
		return new Matrix3(values.clone());
	}

	public double[] toArray() {
		return values.clone();
	}

	public Matrix4 toHomogeneous() {
		return Matrix4.create(new double[] { values[0], values[1], values[2],
				0, values[3], values[4], values[5], 0, values[6], values[7],
				values[8], 0, 0, 0, 0, 1 });
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
		return new Matrix3(new double[] { values[0] * a, values[1] * a,
				values[2] * a, values[3] * a, values[4] * a, values[5] * a,
				values[6] * a, values[7] * a, values[8] * a });
	}

	public Vector3 by(Vector3 v) {
		return new Vector3(values[0] * v.x + values[1] * v.y + values[2] * v.z,
				values[3] * v.x + values[4] * v.y + values[5] * v.z, values[6]
						* v.x + values[7] * v.y + values[8] * v.z);
	}

	public Matrix3 by(Matrix3 matrix) {
		return new Matrix3(new double[] {
				values[0] * matrix.values[0] + values[1] * matrix.values[3]
						+ values[2] * matrix.values[6],
				values[0] * matrix.values[1] + values[1] * matrix.values[4]
						+ values[2] * matrix.values[7],
				values[0] * matrix.values[2] + values[1] * matrix.values[5]
						+ values[2] * matrix.values[8],
				values[3] * matrix.values[0] + values[4] * matrix.values[3]
						+ values[5] * matrix.values[6],
				values[3] * matrix.values[1] + values[4] * matrix.values[4]
						+ values[5] * matrix.values[7],
				values[3] * matrix.values[2] + values[4] * matrix.values[5]
						+ values[5] * matrix.values[8],
				values[6] * matrix.values[0] + values[7] * matrix.values[3]
						+ values[8] * matrix.values[6],
				values[6] * matrix.values[1] + values[7] * matrix.values[4]
						+ values[8] * matrix.values[7],
				values[6] * matrix.values[2] + values[7] * matrix.values[5]
						+ values[8] * matrix.values[8] });
	}

	public Matrix3 transpose() {
		return new Matrix3(new double[] { values[0], values[3], values[6],
				values[1], values[4], values[7], values[2], values[5],
				values[8] });
	}

	public Matrix3 invert() {
		final double determinant = determinant();
		return new Matrix3(new double[] {
				(values[4] * values[8] - values[7] * values[5]) / determinant,
				(values[2] * values[7] - values[8] * values[1]) / determinant,
				(values[1] * values[5] - values[4] * values[2]) / determinant,
				(values[5] * values[6] - values[8] * values[3]) / determinant,
				(values[0] * values[8] - values[6] * values[2]) / determinant,
				(values[2] * values[3] - values[5] * values[0]) / determinant,
				(values[3] * values[7] - values[6] * values[4]) / determinant,
				(values[1] * values[6] - values[7] * values[0]) / determinant,
				(values[0] * values[4] - values[3] * values[1]) / determinant });
	}

	public Matrix3 rotate(double x, double y, double z, double a) {
		return by(Matrix3.createRotation(x, y, z, a));
	}
}
