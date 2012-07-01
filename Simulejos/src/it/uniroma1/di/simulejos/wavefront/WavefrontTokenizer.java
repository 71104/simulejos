package it.uniroma1.di.simulejos.wavefront;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

public class WavefrontTokenizer {
	private final StreamTokenizer tokenizer;

	public WavefrontTokenizer(Reader reader) {
		this.tokenizer = new StreamTokenizer(reader);
		this.tokenizer.commentChar('#');
		this.tokenizer.eolIsSignificant(true);
		this.tokenizer.lowerCaseMode(false);
		this.tokenizer.slashStarComments(false);
	}

	public class ParseException extends Exception {
		private static final long serialVersionUID = 5429017724397647795L;

		private ParseException() {
			super("syntax error at line " + tokenizer.lineno());
		}
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
			throw new ParseException();
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
			throw new ParseException();
		}
	}

	public float readFloat() throws IOException, ParseException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			throw new ParseException();
		}
		return (float) tokenizer.nval;
	}

	public short readShort() throws IOException, ParseException {
		if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
			throw new ParseException();
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
				throw new ParseException();
			}
			readShort();
		}
		return index;
	}
}
