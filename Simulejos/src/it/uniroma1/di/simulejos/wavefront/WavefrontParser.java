package it.uniroma1.di.simulejos.wavefront;

import it.uniroma1.di.simulejos.wavefront.WavefrontCommandHandler.Corner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WavefrontParser {
	private final boolean swapYAndZ;
	private final File sourceFile;
	private final WavefrontCommandHandler handler;
	private final StreamTokenizer tokenizer;

	private int vertexCount;

	public WavefrontParser(File file, WavefrontCommandHandler handler,
			boolean swapYAndZ) throws FileNotFoundException {
		this.swapYAndZ = swapYAndZ;
		this.sourceFile = file;
		this.handler = handler;
		this.tokenizer = new StreamTokenizer(new FileReader(file));
		this.tokenizer.eolIsSignificant(true);
		this.tokenizer.lowerCaseMode(false);
		this.tokenizer.slashSlashComments(false);
		this.tokenizer.slashStarComments(false);
		this.tokenizer.ordinaryChar('/');
		this.tokenizer.commentChar('#');
	}

	private String readKeyword() throws IOException, ParseException {
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

	private void skipLine() throws IOException {
		int token;
		do {
			token = tokenizer.nextToken();
		} while ((token != StreamTokenizer.TT_EOL)
				&& (token != StreamTokenizer.TT_EOF));
	}

	private boolean isEol() throws IOException {
		final int token = tokenizer.nextToken();
		tokenizer.pushBack();
		if ((token != StreamTokenizer.TT_EOL)
				&& (token != StreamTokenizer.TT_EOF)) {
			return false;
		} else {
			return true;
		}
	}

	private void skipEol() throws IOException, ParseException {
		final int token = tokenizer.nextToken();
		if ((token != StreamTokenizer.TT_EOL)
				&& (token != StreamTokenizer.TT_EOF)) {
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"end of line expected");
		}
	}

	private double readDouble() throws IOException, ParseException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"number expected");
		}
		return tokenizer.nval;
	}

	private Double readOptionalDouble() throws IOException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			tokenizer.pushBack();
			return null;
		}
		return tokenizer.nval;
	}

	private int readShort() throws IOException, ParseException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"number expected");
		}
		return (int) tokenizer.nval;
	}

	private Corner readCorner() throws IOException, ParseException {
		int index = readShort();
		if (index > vertexCount) {
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"invalid index");
		}
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
		return new Corner(index - 1, 0, 0); // FIXME
	}

	public void parse() throws IOException, ParseException {
		vertexCount = 0;
		String keyword;
		while ((keyword = readKeyword()) != null) {
			if (keyword.equals("v")) {
				final double x = readDouble();
				final double y = readDouble();
				final double z = readDouble();
				final Double d = readOptionalDouble();
				final double w;
				if (d != null) {
					w = d;
				} else {
					w = 1;
				}
				if (swapYAndZ) {
					handler.vertex(x, z, y, w);
				} else {
					handler.vertex(x, y, z, w);
				}
				vertexCount++;
				skipEol();
			} else if (keyword.equals("vn")) {
				final double x = readDouble();
				final double y = readDouble();
				final double z = readDouble();
				if (swapYAndZ) {
					handler.normal(x, z, y);
				} else {
					handler.normal(x, y, z);
				}
				skipEol();
			} else if (keyword.equals("f")) {
				final List<Corner> corners = new LinkedList<Corner>();
				corners.add(readCorner());
				corners.add(readCorner());
				corners.add(readCorner());
				while (!isEol()) {
					corners.add(readCorner());
				}
				if (!swapYAndZ) {
					Collections.reverse(corners);
				}
				handler.face(corners.toArray(new Corner[corners.size()]));
				skipEol();
			} else {
				skipLine();
			}
		}
	}
}
