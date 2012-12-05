package it.uniroma1.di.simulejos.math;

import java.io.Serializable;

/**
 * Represents a pair of real values, or a point in the two-dimensional plane.
 * 
 * Object of the {@link Vector2} class are immutable.
 * 
 * @author Alberto La Rocca
 */
public class Vector2 implements Cloneable, Serializable {
	private static final long serialVersionUID = 4822298356837119742L;

	/**
	 * The X component of this vector.
	 */
	public final double x;

	/**
	 * The Y component of this vector.
	 */
	public final double y;

	/**
	 * The (0, 0) vector.
	 */
	public static final Vector2 NULL = new Vector2(0, 0);

	/**
	 * The (1, 0) vector.
	 */
	public static final Vector2 I = new Vector2(1, 0);

	/**
	 * The (0, 1) vector.
	 */
	public static final Vector2 J = new Vector2(0, 1);

	/**
	 * Constructs a {@link Vector2} with the specified X and Y components.
	 * 
	 * @param x
	 *            The X component.
	 * @param y
	 *            The Y component.
	 */
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Vector2 other = (Vector2) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public Vector2 clone() {
		return new Vector2(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	/**
	 * Returns a new array containing two <code>double</code> values, the two
	 * components of this vector. Modification to the array will not have any
	 * effect on this vector.
	 * 
	 * @return A new array of <code>double</code> containing the two components
	 *         of this vector.
	 */
	public double[] toArray() {
		return new double[] { x, y };
	}

	/**
	 * Converts this 2D standard vector into a 2D homogeneous vector represented
	 * by a {@link Vector3} object with three components: X, Y and the W
	 * homogeneous coordinate.
	 * 
	 * This {@link Vector2} object is not modified, a new {@link Vector3} is
	 * created.
	 * 
	 * @return A new {@link Vector3} object representing a 2D vector in
	 *         homogeneous coordinates.
	 */
	public Vector3 toHomogeneous() {
		return new Vector3(x, y, 1);
	}

	/**
	 * Floors the X component of this vector to the specified value.
	 * 
	 * This vector is not modified, a new {@link Vector2} is created and
	 * returned instead.
	 * 
	 * @param min
	 *            A lower bound for this vector's X component.
	 * @return A new {@link Vector2} whose X component is greater than or equal
	 *         to the specified minimum value.
	 */
	public Vector2 floorX(double min) {
		return new Vector2(Math.max(x, min), y);
	}

	/**
	 * Floors the Y component of this vector to the specified value.
	 * 
	 * This vector is not modified, a new {@link Vector2} is created and
	 * returned instead.
	 * 
	 * @param min
	 *            A lower bound for this vector's Y component.
	 * @return A new {@link Vector2} whose Y component is greater than or equal
	 *         to the specified minimum value.
	 */
	public Vector2 floorY(double min) {
		return new Vector2(x, Math.max(y, min));
	}

	/**
	 * Floors both the components of this vector to the corresponding components
	 * of the specified vector.
	 * 
	 * This vector is not modified, a new {@link Vector2} is created and
	 * returned instead.
	 * 
	 * @param min
	 *            A {@link Vector2} object whose {@link #x} and {@link #y}
	 *            components are used as lower bounds for this vector's ones.
	 * @return A new {@link Vector2} whose X and Y components are greater than
	 *         or equal to those of the specified vector.
	 */
	public Vector2 floor(Vector2 min) {
		return new Vector2(Math.max(x, min.x), Math.max(y, min.y));
	}

	public Vector2 ceilX(double max) {
		return new Vector2(Math.min(x, max), y);
	}

	public Vector2 ceilY(double max) {
		return new Vector2(x, Math.min(y, max));
	}

	public Vector2 ceil(Vector2 max) {
		return new Vector2(Math.min(x, max.x), Math.min(y, max.y));
	}

	private static double clamp(double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
	}

	public Vector2 clampX(double min, double max) {
		return new Vector2(clamp(x, min, max), y);
	}

	public Vector2 clampY(double min, double max) {
		return new Vector2(x, clamp(y, min, max));
	}

	public Vector2 clamp(Vector2 min, Vector2 max) {
		return new Vector2(clamp(x, min.x, max.x), clamp(y, min.y, max.y));
	}

	public double length() {
		return Math.hypot(x, y);
	}

	public double distance(Vector2 v) {
		return Math.hypot(x - v.x, y - v.y);
	}

	public Vector2 normalize() {
		final double h = Math.hypot(x, y);
		return new Vector2(x / h, y / h);
	}

	public Vector2 invert() {
		return new Vector2(-x, -y);
	}

	public Vector2 plus(Vector2 v) {
		return new Vector2(x + v.x, y + v.y);
	}

	public Vector2 minus(Vector2 v) {
		return new Vector2(x - v.x, y - v.y);
	}

	public Vector2 by(double f) {
		return new Vector2(x * f, y * f);
	}

	public Vector2 div(double f) {
		return new Vector2(x / f, y / f);
	}

	public double dot(Vector2 v) {
		return x * v.x + y * v.y;
	}

	public double angleWith(Vector2 v) {
		return Math.acos(dot(v) / (length() * v.length()));
	}

	public Vector2 reflect(Vector2 normal) {
		return minus(normal.by(2 * dot(normal)));
	}

	public Vector2 refract(Vector2 normal, double eta) {
		final double k = 1 - eta * eta * (1 - Math.pow(dot(normal), 2));
		if (k < 0) {
			return NULL;
		} else {
			return by(eta).minus(normal.by(eta * dot(normal) + Math.sqrt(k)));
		}
	}
}
