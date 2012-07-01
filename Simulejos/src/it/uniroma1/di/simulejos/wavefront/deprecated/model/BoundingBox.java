package it.uniroma1.di.simulejos.wavefront.deprecated.model;

import it.uniroma1.di.simulejos.math.Vector3;

public final class BoundingBox {
	public static final BoundingBox NULL_BOUNDING_BOX;

	static {
		NULL_BOUNDING_BOX = new BoundingBox(new Vector3(0.5, 0.5, 0.5));
		NULL_BOUNDING_BOX.test(new Vector3(-0.5, -0.5, -0.5));
	}

	private volatile Vector3 min;
	private volatile Vector3 max;

	public BoundingBox(Vector3 first) {
		min = first;
		max = first;
	}

	public void test(Vector3 test) {
		min = min.ceil(test);
		max = max.floor(test);
	}

	public double getMaxSize() {
		double width = max.x - min.x;
		double height = max.y - min.y;

		double maxSize;
		if (height > width) {
			maxSize = height;
		} else {
			maxSize = width;
		}

		double depth = max.z - min.z;
		if (depth > maxSize) {
			maxSize = depth;
		}

		return maxSize;
	}

	public Vector3 getCenter() {
		return new Vector3((min.x + max.x) / 2.0, (min.y + max.y) / 2.0,
				(min.z + max.z) / 2.0);
	}
}
