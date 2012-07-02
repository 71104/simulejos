package it.uniroma1.di.simulejos.wavefront;

import it.uniroma1.di.simulejos.wavefront.WavefrontCommandHandler.Corner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

public class WavefrontParser {
	private final File sourceFile;
	private final WavefrontCommandHandler handler;
	private final StreamTokenizer tokenizer;

	public WavefrontParser(File file, WavefrontCommandHandler handler)
			throws FileNotFoundException {
		this.sourceFile = file;
		this.handler = handler;
		this.tokenizer = new StreamTokenizer(new FileReader(file));
		this.tokenizer.commentChar('#');
		this.tokenizer.eolIsSignificant(true);
		this.tokenizer.lowerCaseMode(false);
		this.tokenizer.slashStarComments(false);
	}

	public String readKeyword() throws IOException, ParseException {
		int token;
		do {
			token = tokenizer.nextToken();
		} while (token == StreamTokenizer.TT_EOL);
		switch (token) {
		case StreamTokenizer.TT_EOF:
			return null;
		case StreamTokenizer.TT_WORD:
			return tokenizer.sval;
		default:
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"keyword expected");
		}
	}

	public void skipLine() throws IOException {
		int token;
		do {
			token = tokenizer.nextToken();
		} while ((token != StreamTokenizer.TT_EOL)
				&& (token != StreamTokenizer.TT_EOF));
	}

	public void skipEol() throws IOException, ParseException {
		final int token = tokenizer.nextToken();
		if ((token != StreamTokenizer.TT_EOL)
				&& (token != StreamTokenizer.TT_EOF)) {
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"end of line expected");
		}
	}

	public double readDouble() throws IOException, ParseException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"number expected");
		}
		return tokenizer.nval;
	}

	public Double readOptionalDouble() throws IOException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			tokenizer.pushBack();
			return null;
		}
		return tokenizer.nval;
	}

	public int readShort() throws IOException, ParseException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"number expected");
		}
		return (int) tokenizer.nval;
	}

	public Corner readCorner() throws IOException, ParseException {
		int index = readShort();
		if (tokenizer.nextToken() != '/') {
			tokenizer.pushBack();
		} else {
			int token = tokenizer.nextToken();
			if (token == StreamTokenizer.TT_NUMBER) {
				token = tokenizer.nextToken();
			}
			if (token != '/') {
				throw new ParseException(sourceFile, tokenizer.lineno(),
						"/ expected");
			}
			readShort();
		}
		return new Corner(index, 0, 0); // FIXME
	}

	public void parse() throws IOException, ParseException {
		String keyword;
		while ((keyword = readKeyword()) != null) {
			if (keyword == "v") {
				final double x = readDouble();
				final double y = readDouble();
				final double z = readDouble();
				final Double w = readOptionalDouble();
				if (w != null) {
					handler.vertex(x, y, z, w);
				} else {
					handler.vertex(x, y, z, 1);
				}
				skipEol();
			} else if (keyword == "vn") {
				final double x = readDouble();
				final double y = readDouble();
				final double z = readDouble();
				handler.normal(x, y, z);
				skipEol();
			} else if (keyword == "f") {
				final Corner a = readCorner();
				final Corner b = readCorner();
				final Corner c = readCorner();
				// TODO read further optional corners
				handler.face(a, b, c);
				skipEol();
			} else {
				skipLine();
			}
		}
	}
}
