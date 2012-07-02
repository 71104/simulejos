package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.util.DynamicFloatArray;
import it.uniroma1.di.simulejos.util.DynamicShortArray;
import it.uniroma1.di.simulejos.wavefront.ParseException;
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
		this.vertices = FloatBuffer.wrap(vertices).asReadOnlyBuffer();
		this.indices = ShortBuffer.wrap(indices).asReadOnlyBuffer();
	}

	public static ModelData parseWavefront(File file) throws IOException,
			ParseException {
		final DynamicFloatArray vertices = new DynamicFloatArray();
		final DynamicShortArray indices = new DynamicShortArray();
		final WavefrontParser tokenizer = new WavefrontParser(file);
		String keyword;
		while ((keyword = tokenizer.readKeyword()) != null) {
			if (keyword == "v") {
				vertices.append(tokenizer.readFloat());
				vertices.append(tokenizer.readFloat());
				vertices.append(tokenizer.readFloat());
				final Float w = tokenizer.readOptionalFloat();
				if (w != null) {
					vertices.append(w);
				} else {
					vertices.append(1);
				}
				tokenizer.skipEol();
			} else if (keyword == "f") {
				indices.append(tokenizer.readCorner());
				indices.append(tokenizer.readCorner());
				indices.append(tokenizer.readCorner());
				// TODO read optional fourth corner
				tokenizer.skipEol();
			} else {
				tokenizer.skipLine();
			}
		}
		return new ModelData(vertices.trim(), indices.trim());
	}
}
