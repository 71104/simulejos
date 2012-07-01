package it.uniroma1.di.simulejos;

import it.uniroma1.di.simulejos.util.DynamicFloatArray;
import it.uniroma1.di.simulejos.util.DynamicShortArray;
import it.uniroma1.di.simulejos.wavefront.WavefrontTokenizer;
import it.uniroma1.di.simulejos.wavefront.WavefrontTokenizer.ParseException;

import java.io.IOException;
import java.io.Reader;
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

	public static ModelData parseWavefront(Reader reader) throws IOException,
			ParseException {
		final DynamicFloatArray vertices = new DynamicFloatArray();
		final DynamicShortArray indices = new DynamicShortArray();
		final WavefrontTokenizer tokenizer = new WavefrontTokenizer(reader);
		String keyword;
		while ((keyword = tokenizer.readKeyword()) != null) {
			if (keyword == "v") {
				vertices.append(tokenizer.readFloat());
				vertices.append(tokenizer.readFloat());
				vertices.append(tokenizer.readFloat());
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
