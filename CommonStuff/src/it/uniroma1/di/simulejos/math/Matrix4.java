package it.uniroma1.di.simulejos.math;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents an immutable 4x4 matrix.
 * 
 * @author Alberto La Rocca
 */
public class Matrix4 implements Cloneable, Serializable {
	private static final long serialVersionUID = -3293211504653809206L;

	/**
	 * A null 4x4 matrix.
	 */
	public static final Matrix4 NULL = new Matrix4(new double[] { 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });

	/**
	 * The 4x4 identity matrix.
	 */
	public static final Matrix4 IDENTITY = new Matrix4(new double[] { 1, 0, 0,
			0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 });

	private final double[] values;

	private Matrix4(double[] values) {
		if (values.length != 16) {
			throw new IllegalArgumentException();
		}
		this.values = values;
	}

	/**
	 * Creates a matrix based on the specified real values. Values must be
	 * specified row-first and must be 16.
	 * 
	 * Modifications to the specified array will not affect the content of the
	 * newly created matrix.
	 * 
	 * @param values
	 *            An array of 16 real values representing the values to put in
	 *            the new matrix, specified in row-first order.
	 * @return The newly created {@link Matrix4} object.
	 */
	public static Matrix4 create(double[] values) {
		return new Matrix4(values.clone());
	}

	/**
	 * Creates a 4x4 homogeneous matrix from a specified 3x3 matrix.
	 * 
	 * The returned matrix is identical to this matrix in the upper left 3x3
	 * block, has a 1 in the lower right cell and is otherwise zeroed.
	 * 
	 * @param matrix
	 *            A 3x3 matrix.
	 * @return A new {@link Matrix4} object.
	 */
	public static Matrix4 create(Matrix3 matrix) {
		final double values[] = new double[16];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				values[i * 4 + j] = matrix.getAt(i, j);
			}
		}
		values[15] = 1;
		return new Matrix4(values);
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
	 * Returns the value at the specified cell coordinates in this matrix.
	 * 
	 * Both <code>i</code> and <code>j</code> must be within the range [0, 3],
	 * otherwise an {@link IndexOutOfBoundsException} will be thrown.
	 * 
	 * @param i
	 *            The row index.
	 * @param j
	 *            The column index.
	 * @return The real value at the specified coordinates.
	 */
	public double getAt(int i, int j) {
		return values[i * 4 + j];
	}

	// TODO
}
