package it.uniroma1.di.simulejos.math;

import java.io.Serializable;

/**
 * Describes the bounding box of a three-dimensional mesh of points in the
 * space.
 * 
 * @author Alberto La Rocca
 */
public class BoundingBox implements Cloneable, Serializable {
	private static final long serialVersionUID = 8761446190649778946L;

	/**
	 * The lower front left corner of the box. The three values contained in
	 * this {@link Vector3} object are the overall lowest X, Y and Z values of
	 * all the points in the original mesh.
	 */
	public final Vector3 min;

	/**
	 * The upper right back corner of the box. The three values contained in
	 * this {@link Vector3} object are the overall highest X, Y and Z values of
	 * all the points in the original mesh.
	 */
	public final Vector3 max;

	/**
	 * A {@link Vector3} object containing the size of the box along the X, Y
	 * and Z axes.
	 */
	public final Vector3 size;

	/**
	 * The center of the box. This is equivalent to
	 * <code>max.plus(min).div(2)</code>.
	 */
	public final Vector3 center;

	/**
	 * Directly constructs a {@link BoundingBox} by specifying its {@link #min}
	 * and {@link #max} fields and subsequently computing {@link #size} and
	 * {@link #center} instead of analytizing a mesh.
	 * 
	 * @param min
	 *            The lower front left corner of the box. It will be assigned to
	 *            the {@link #min} field.
	 * @param max
	 *            The upper right back corner of the box. It will be assigned to
	 *            the {@link #max} field.
	 */
	public BoundingBox(Vector3 min, Vector3 max) {
		this.min = min;
		this.max = max;
		this.size = max.minus(min);
		this.center = min.plus(max).by(0.5);
	}

	/**
	 * Constructs a {@link BoundingBox} by analytizing a three-dimensional mesh
	 * of points, or vertices, in the space and computing the smallest possible
	 * orthogonal box containing all the points.
	 * 
	 * @param vertices
	 *            A flattened array of vertex coordinates: each triple of
	 *            subsequent floating point elements represents the X, Y and Z
	 *            (respectively) coordinates of a point in the space.
	 */
	public BoundingBox(float[] vertices) {
		if (vertices.length < 4) {
			throw new IllegalArgumentException();
		}
		Vector3 min = new Vector3(vertices[0] / vertices[3], vertices[1]
				/ vertices[3], vertices[2] / vertices[3]);
		Vector3 max = min;
		for (int i = 4; i < vertices.length / 4; i++) {
			min = min.ceil(new Vector3(vertices[i * 4] / vertices[i * 4 + 3],
					vertices[i * 4 + 1] / vertices[i * 4 + 3],
					vertices[i * 4 + 2] / vertices[i * 4 + 3]));
			max = max.floor(new Vector3(vertices[i * 4] / vertices[i * 4 + 3],
					vertices[i * 4 + 1] / vertices[i * 4 + 3],
					vertices[i * 4 + 2] / vertices[i * 4 + 3]));
		}
		this.min = min;
		this.max = max;
		this.size = max.minus(min);
		this.center = min.plus(max).by(0.5);
	}

	/**
	 * Returns a boolean value indicating whether the specified point is
	 * contained in this bounding box.
	 * 
	 * @param v
	 *            A point in the three-dimensional space.
	 * @return <code>true</code> if the specified point is contained in this
	 *         bounding box, <code>false</code> otherwise.
	 */
	public boolean contains(Vector3 v) {
		return (v.x >= min.x) && (v.x <= max.x) && (v.y >= min.y)
				&& (v.y <= max.y) && (v.z >= min.z) && (v.z <= max.z);
	}

	/**
	 * Returns the maximum span of the box along the X, Y or Z axis. In
	 * pseudocode, this method computes
	 * <code>Math.max(size.x, size.y, size.z)</code>.
	 * 
	 * @return The maximum span of the box along the X, Y or Z axis.
	 */
	public double getMaxSpan() {
		if (size.x > size.y) {
			if (size.x > size.z) {
				return size.x;
			} else {
				return size.z;
			}
		} else {
			if (size.y > size.z) {
				return size.y;
			} else {
				return size.z;
			}
		}
	}
}
