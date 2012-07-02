package it.uniroma1.di.simulejos.wavefront;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

public class WavefrontParser {
	private final File sourceFile;
	private final StreamTokenizer tokenizer;

	public WavefrontParser(File file) throws FileNotFoundException {
		this.sourceFile = file;
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

	public float readFloat() throws IOException, ParseException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"number expected");
		}
		return (float) tokenizer.nval;
	}

	public Float readOptionalFloat() throws IOException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			tokenizer.pushBack();
			return null;
		}
		return (float) tokenizer.nval;
	}

	public short readShort() throws IOException, ParseException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			throw new ParseException(sourceFile, tokenizer.lineno(),
					"number expected");
		}
		return (short) tokenizer.nval;
	}

	public short readCorner() throws IOException, ParseException {
		short index = readShort();
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
		return index;
	}
}
