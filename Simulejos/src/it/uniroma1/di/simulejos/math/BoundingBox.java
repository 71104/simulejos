package it.uniroma1.di.simulejos.math;

import java.io.Serializable;

public class BoundingBox implements Cloneable, Serializable {
	private static final long serialVersionUID = 8761446190649778946L;

	public final Vector3 min;
	public final Vector3 max;

	public BoundingBox(float[] vertices) {
		if (vertices.length < 3) {
			throw new IllegalArgumentException();
		}
		Vector3 min = new Vector3(vertices[0], vertices[1], vertices[2]);
		Vector3 max = min;
		for (int i = 3; i < vertices.length / 3; i++) {
			min = min.floor(new Vector3(vertices[i * 3], vertices[i * 3 + 1],
					vertices[i * 3 + 2]));
			max = max.ceil(new Vector3(vertices[i * 3], vertices[i * 3 + 1],
					vertices[i * 3 + 2]));
		}
		this.min = min;
		this.max = max;
	}
}
