package it.uniroma1.di.simulejos.math;

import java.io.Serializable;

public class BoundingBox implements Cloneable, Serializable {
	private static final long serialVersionUID = 8761446190649778946L;

	public final Vector3 min;
	public final Vector3 max;
	public final Vector3 size;
	public final Vector3 center;

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

	public boolean contains(Vector3 v) {
		return (v.x >= min.x) && (v.x <= max.x) && (v.y >= min.y)
				&& (v.y <= max.y) && (v.z >= min.z) && (v.z <= max.z);
	}
}
