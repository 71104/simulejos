package it.uniroma1.di.simulejos.wavefront.model;

import it.uniroma1.di.simulejos.math.Vector3;

public final class Color implements Cloneable {
	public final double r;
	public final double g;
	public final double b;

	public Color(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color(Vector3 v) {
		this.r = v.x;
		this.g = v.y;
		this.b = v.z;
	}

	@Override
	public Color clone() {
		return new Color(r, g, b);
	}

	@Override
	public String toString() {
		return "(" + r + ", " + g + ", " + b + ")";
	}

	public double[] toArray() {
		return new double[] { r, g, b };
	}
}
