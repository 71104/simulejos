package it.uniroma1.di.simulejos;

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
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ModelData implements Serializable {
	private static final long serialVersionUID = 3141711160723266117L;

	public final int count;
	public final FloatBuffer vertices;
	public final ShortBuffer indices;

	public ModelData(float[] vertices, short[] indices) {
		this.count = indices.length;
		this.vertices = FloatBuffer.wrap(vertices);
		this.indices = ShortBuffer.wrap(indices);
	}

	public static ModelData parseWavefront(File file) throws IOException,
			ParseException {
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
		}).parse();
		return new ModelData(vertices.trim(), indices.trim());
	}
}
