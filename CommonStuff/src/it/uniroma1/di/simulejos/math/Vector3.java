package it.uniroma1.di.simulejos.math;

import java.io.Serializable;

public class Vector3 implements Cloneable, Serializable {
	private static final long serialVersionUID = 4822298356837119742L;

	public final double x;
	public final double y;
	public final double z;

	public static final Vector3 NULL = new Vector3(0, 0, 0);
	public static final Vector3 I = new Vector3(1, 0, 0);
	public static final Vector3 J = new Vector3(0, 1, 0);
	public static final Vector3 K = new Vector3(0, 0, 1);

	public static final Vector3 BLACK = new Vector3(0, 0, 0);
	public static final Vector3 WHITE = new Vector3(1, 1, 1);
	public static final Vector3 RED = new Vector3(1, 0, 0);
	public static final Vector3 GREEN = new Vector3(0, 1, 0);
	public static final Vector3 BLUE = new Vector3(0, 0, 1);

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
		temp = Double.doubleToLongBits(z);
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
		Vector3 other = (Vector3) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	@Override
	public Vector3 clone() {
		return new Vector3(x, y, z);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public double[] toArray() {
		return new double[] { x, y, z };
	}

	public Vector4 toHomogeneous() {
		return new Vector4(x, y, z, 1);
	}

	public Vector3 floorX(double min) {
		return new Vector3(Math.max(x, min), y, z);
	}

	public Vector3 floorY(double min) {
		return new Vector3(x, Math.max(y, min), z);
	}

	public Vector3 floorZ(double min) {
		return new Vector3(x, y, Math.max(z, min));
	}

	public Vector3 floor(Vector3 min) {
		return new Vector3(Math.max(x, min.x), Math.max(y, min.y), Math.max(z,
				min.z));
	}

	public Vector3 ceilX(double max) {
		return new Vector3(Math.min(x, max), y, z);
	}

	public Vector3 ceilY(double max) {
		return new Vector3(x, Math.min(y, max), z);
	}

	public Vector3 ceilZ(double max) {
		return new Vector3(x, y, Math.min(z, max));
	}

	public Vector3 ceil(Vector3 max) {
		return new Vector3(Math.min(x, max.x), Math.min(y, max.y), Math.min(z,
				max.z));
	}

	private static double clamp(double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
	}

	public Vector3 clampX(double min, double max) {
		return new Vector3(clamp(x, min, max), y, z);
	}

	public Vector3 clampY(double min, double max) {
		return new Vector3(x, clamp(y, min, max), z);
	}

	public Vector3 clampZ(double min, double max) {
		return new Vector3(x, y, clamp(z, min, max));
	}

	public Vector3 clamp(Vector3 min, Vector3 max) {
		return new Vector3(clamp(x, min.x, max.x), clamp(y, min.y, max.y),
				clamp(z, min.z, max.z));
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public double distance(Vector3 v) {
		return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2)
				+ Math.pow(z - v.z, 2));
	}

	public Vector3 normalize() {
		final double h = Math.hypot(x, y);
		return new Vector3(x / h, y / h, z / h);
	}

	public Vector3 invert() {
		return new Vector3(-x, -y, -z);
	}

	public Vector3 plus(Vector3 v) {
		return new Vector3(x + v.x, y + v.y, z + v.z);
	}

	public Vector3 minus(Vector3 v) {
		return new Vector3(x - v.x, y - v.y, z - v.z);
	}

	public Vector3 by(double f) {
		return new Vector3(x * f, y * f, z * f);
	}

	public Vector3 div(double f) {
		return new Vector3(x / f, y / f, z / f);
	}

	public double dot(Vector3 v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public double angleWith(Vector3 v) {
		return Math.acos(dot(v) / (length() * v.length()));
	}

	public Vector3 cross(Vector3 v) {
		return new Vector3(y * v.z - z * v.y, x * v.z - z * v.x, x * v.y - y
				* v.x);
	}

	public Vector3 reflect(Vector3 normal) {
		return minus(normal.by(2 * dot(normal)));
	}

	public Vector3 refract(Vector3 normal, double eta) {
		final double k = 1 - eta * eta * (1 - Math.pow(dot(normal), 2));
		if (k < 0) {
			return NULL;
		} else {
			return by(eta).minus(normal.by(eta * dot(normal) + Math.sqrt(k)));
		}
	}
}
