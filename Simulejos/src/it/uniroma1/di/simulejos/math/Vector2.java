package it.uniroma1.di.simulejos.math;

import java.io.Serializable;

public class Vector2 implements Cloneable, Serializable {
	private static final long serialVersionUID = 4822298356837119742L;

	public final double x;
	public final double y;

	public static final Vector2 NULL = new Vector2(0, 0);
	public static final Vector2 I = new Vector2(1, 0);
	public static final Vector2 J = new Vector2(0, 1);

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

	public double[] toArray() {
		return new double[] { x, y };
	}

	public Vector3 toHomogeneous() {
		return new Vector3(x, y, 1);
	}

	public Vector2 floorX(double min) {
		return new Vector2(Math.max(x, min), y);
	}

	public Vector2 floorY(double min) {
		return new Vector2(x, Math.max(y, min));
	}

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
