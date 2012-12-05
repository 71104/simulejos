package it.uniroma1.di.simulejos.math;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents an immutable 3x3 matrix.
 * 
 * @author Alberto La Rocca
 */
public class Matrix3 implements Cloneable, Serializable {
	private static final long serialVersionUID = 4389630396481063939L;

	/**
	 * A null 3x3 matrix.
	 */
	public static final Matrix3 NULL = new Matrix3(new double[] { 0, 0, 0, 0,
			0, 0, 0, 0, 0 });

	/**
	 * The 3x3 identity matrix.
	 */
	public static final Matrix3 IDENTITY = new Matrix3(new double[] { 1, 0, 0,
			0, 1, 0, 0, 0, 1 });

	private final double values[];

	private Matrix3(double[] values) {
		if (values.length != 9) {
			throw new IllegalArgumentException();
		}
		this.values = values;
	}

	/**
	 * Creates a matrix based on the specified real values. Values must be
	 * specified row-first and must be 9.
	 * 
	 * Modifications to the specified array will not affect the content of the
	 * newly created matrix.
	 * 
	 * @param values
	 *            An array of 9 real values representing the values to put in
	 *            the new matrix, specified in row-first order.
	 * @return The newly created {@link Matrix3} object.
	 */
	public static Matrix3 create(double[] values) {
		return new Matrix3(values.clone());
	}

	/**
	 * Creates a rotation matrix based on the specified parameters.
	 * 
	 * The <code>x</code>, <code>y</code> and <code>z</code> arguments describe
	 * a unit-length vector representing the rotation axis, while the
	 * <code>a</code> argument is the rotation angle, in radians.
	 * 
	 * @param x
	 *            The X component of a unit-length vector describing the
	 *            rotation axis.
	 * @param y
	 *            The Y component of a unit-length vector describing the
	 *            rotation axis.
	 * @param z
	 *            The Z component of a unit-length vector describing the
	 *            rotation axis.
	 * @param a
	 *            The rotation angle, in radians.
	 * @return The newly created {@link Matrix3} object.
	 */
	public static Matrix3 createRotation(double x, double y, double z, double a) {
		final double s = Math.sin(a);
		final double c = Math.cos(a);
		return new Matrix3(new double[] { c + x * x * (1 - c),
				y * x * (1 - c) + z * s, z * x * (1 - c) - y * s,
				x * y * (1 - c) - z * s, c + y * y * (1 - c),
				z * y * (1 - c) + x * s, x * z * (1 - c) + y * s,
				y * z * (1 - c) - x * s, c + z * z * (1 - c) });
	}

	/**
	 * Creates a scaling matrix, a matrix that transforms vectors multiplying
	 * each component by a factor.
	 * 
	 * @param x
	 *            The X factor.
	 * @param y
	 *            The Y factor.
	 * @param z
	 *            The Z factor.
	 * @return The newly created {@link Matrix3} object.
	 */
	public static Matrix3 createScaling(double x, double y, double z) {
		return new Matrix3(new double[] { x, 0, 0, 0, y, 0, 0, 0, z });
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

	/**
	 * Returns the real values contained in this matrix as an array and in
	 * row-first order. Modifications to the returned array will not affect this
	 * matrix.
	 * 
	 * @return An array containing the real values of this matrix, in row-first
	 *         order.
	 */
	public double[] toArray() {
		return values.clone();
	}

	/**
	 * Converts this 3x3 matrix to a 4x4 matrix that can be used to transform
	 * homogeneous {@link Vector4} objects.
	 * 
	 * The returned matrix is identical to this matrix in the upper left 3x3
	 * block, has a 1 in the lower right cell and is otherwise zeroed.
	 * 
	 * @return A new {@link Matrix4} object.
	 */
	public Matrix4 toHomogeneous() {
		return Matrix4.create(new double[] { values[0], values[1], values[2],
				0, values[3], values[4], values[5], 0, values[6], values[7],
				values[8], 0, 0, 0, 0, 1 });
	}

	/**
	 * Returns the value at the specified cell coordinates in this matrix.
	 * 
	 * Both <code>i</code> and <code>j</code> must be within the range [0, 2],
	 * otherwise an {@link IndexOutOfBoundsException} will be thrown.
	 * 
	 * @param i
	 *            The row index.
	 * @param j
	 *            The column index.
	 * @return The real value at the specified coordinates.
	 */
	public double getAt(int i, int j) {
		return values[i * 3 + j];
	}

	/**
	 * Computes the determinant of this matrix.
	 * 
	 * @return The determinant of this matrix.
	 */
	public double determinant() {
		return values[0] * (values[4] * values[8] - values[5] * values[7])
				- values[1] * (values[3] * values[8] - values[5] * values[6])
				+ values[2] * (values[3] * values[7] - values[4] * values[6]);
	}

	/**
	 * Multiplies each value of this matrix by a factor.
	 * 
	 * A new matrix is returned, while this one is left unchanged.
	 * 
	 * @param a
	 *            The multiplication factor.
	 * @return A new {@link Matrix3} object representing the multiplied matrix.
	 */
	public Matrix3 by(double a) {
		return new Matrix3(new double[] { values[0] * a, values[1] * a,
				values[2] * a, values[3] * a, values[4] * a, values[5] * a,
				values[6] * a, values[7] * a, values[8] * a });
	}

	/**
	 * Multiplies this 3x3 matrix to the left by a 3x1 matrix specified by a
	 * {@link Vector3} object. The resulting 3x1 matrix is returned as a new
	 * {@link Vector3} object.
	 * 
	 * @param v
	 *            The 3x1 matrix to multiply to the right of this matrix.
	 * @return A new {@link Vector3} object representing the 3x1 product matrix.
	 */
	public Vector3 by(Vector3 v) {
		return new Vector3(values[0] * v.x + values[1] * v.y + values[2] * v.z,
				values[3] * v.x + values[4] * v.y + values[5] * v.z, values[6]
						* v.x + values[7] * v.y + values[8] * v.z);
	}

	/**
	 * Multiplies this 3x3 matrix to the left of another 3x3 matrix.
	 * 
	 * The resulting 3x3 product matrix is returned as a new {@link Matrix3}
	 * object, while this one is left unchanged.
	 * 
	 * @param matrix
	 *            A 3x3 matrix to multiply to the right of this one.
	 * @return A new {@link Matrix3} object representing the 3x3 product matrix.
	 */
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

	/**
	 * Computes the transpose of this matrix.
	 * 
	 * The computed matrix is returned as a new {@link Matrix3} object, while
	 * this one is left unchanged.
	 * 
	 * @return A new {@link Matrix3} object representing the transpose of this
	 *         matrix.
	 */
	public Matrix3 transpose() {
		return new Matrix3(new double[] { values[0], values[3], values[6],
				values[1], values[4], values[7], values[2], values[5],
				values[8] });
	}

	/**
	 * Computes the inverse of this matrix.
	 * 
	 * The computed matrix is returned as a new {@link Matrix3} object, while
	 * this one is left unchanged.
	 * 
	 * @return A new {@link Matrix3} object representing the transpose of this
	 *         matrix.
	 */
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

	/**
	 * Left-multiplies this matrix by a rotation matrix constructed using
	 * {@link #createRotation(double, double, double, double)}. The arguments of
	 * this method have the same meaning as those of
	 * {@link #createRotation(double, double, double, double)}.
	 * 
	 * The returned {@link Matrix3} object is the product matrix. This matrix is
	 * left unchanged.
	 * 
	 * @param x
	 *            The X component of a unit-length vector describing the
	 *            rotation axis.
	 * @param y
	 *            The Y component of a unit-length vector describing the
	 *            rotation axis.
	 * @param z
	 *            The Z component of a unit-length vector describing the
	 *            rotation axis.
	 * @param a
	 *            The rotation angle, in radians.
	 * @return A new {@link Matrix3} representing the product matrix.
	 * @see #createRotation(double, double, double, double)
	 */
	public Matrix3 rotate(double x, double y, double z, double a) {
		return by(Matrix3.createRotation(x, y, z, a));
	}
}
