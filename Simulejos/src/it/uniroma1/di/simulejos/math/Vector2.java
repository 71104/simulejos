package it.uniroma1.di.simulejos.math;

import java.io.Serializable;

public class Vector2 implements Cloneable, Serializable {
	private static final long serialVersionUID = 4822298356837119742L;

	public final double x;
	public final double y;

	public static final Vector2 NULL = new Vector2();
	public static final Vector2 I = new Vector2(1, 0);
	public static final Vector2 J = new Vector2(0, 1);

	public Vector2() {
		this.x = 0;
		this.y = 0;
	}

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
