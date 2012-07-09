package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.math.BoundingBox;
import it.uniroma1.di.simulejos.opengl.Tessellation;
import it.uniroma1.di.simulejos.opengl.Tessellation.Callback;
import it.uniroma1.di.simulejos.util.DynamicFloatArray;
import it.uniroma1.di.simulejos.util.DynamicShortArray;
import it.uniroma1.di.simulejos.wavefront.ParseException;
import it.uniroma1.di.simulejos.wavefront.WavefrontCommandHandler;
import it.uniroma1.di.simulejos.wavefront.WavefrontParser;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class ModelData implements Serializable {
	private static final long serialVersionUID = 3141711160723266117L;

	public final float[] vertices;
	public final short[] indices;

	public ModelData(float[] vertices, short[] indices) {
		this.vertices = new float[vertices.length];
		this.indices = indices;
		final BoundingBox boundingBox = new BoundingBox(vertices);
		final double ratio;
		if ((boundingBox.size.y > boundingBox.size.x)
				&& (boundingBox.size.y > boundingBox.size.z)) {
			ratio = 2 / boundingBox.size.y;
		} else if (boundingBox.size.z > boundingBox.size.x) {
			ratio = 2 / boundingBox.size.z;
		} else {
			ratio = 2 / boundingBox.size.x;
		}
		for (int i = 0; i < vertices.length / 4; i++) {
			this.vertices[i * 4] = (float) ((vertices[i * 4] - boundingBox.center.x) * ratio);
			this.vertices[i * 4 + 1] = (float) ((vertices[i * 4 + 1]
					- boundingBox.min.y - 1) * ratio);
			this.vertices[i * 4 + 2] = (float) ((vertices[i * 4 + 2] - boundingBox.center.z) * ratio);
			this.vertices[i * 4 + 3] = vertices[i * 4 + 3];
		}
	}

	public static ModelData parseWavefront(File file, boolean swapYAndZ)
			throws IOException, ParseException {
		final DynamicFloatArray vertices = new DynamicFloatArray();
		final DynamicShortArray indices = new DynamicShortArray();
		new WavefrontParser(file, new WavefrontCommandHandler() {
			@Override
			public void vertex(double x, double y, double z, double w) {
				vertices.append((float) x);
				vertices.append((float) y);
				vertices.append((float) z);
				vertices.append((float) w);
			}

			@Override
			public void normal(double x, double y, double z) {
			}

			@Override
			public void face(Corner[] corners) {
				final short[] polygonIndices = new short[corners.length];
				int i = 0;
				for (Corner corner : corners) {
					polygonIndices[i++] = (short) corner.vertexIndex;
				}
				new Tessellation(vertices.trim(), polygonIndices,
						new Callback() {
							@Override
							public void index(short index) {
								indices.append(index);
							}
						});
			}
		}, swapYAndZ).parse();
		return new ModelData(vertices.trim(), indices.trim());
	}
}
