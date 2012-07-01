package it.uniroma1.di.simulejos.wavefront.deprecated;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.List;
import java.util.Map;

final class WavefrontParser {
	private final File modelFile;

	private static class WavefrontTokenizer extends StreamTokenizer {
		public WavefrontTokenizer(Reader reader) throws FileNotFoundException {
			super(reader);
			slashSlashComments(false);
			slashStarComments(false);
			ordinaryChar('/');
			commentChar('#');
			wordChars('_', '_');
			eolIsSignificant(true);
			lowerCaseMode(true);
			parseNumbers();
		}
	}

	private final WavefrontTokenizer tokenizer;
	private final Map<String, CommandHandler> handlers;

	public WavefrontParser(File modelFile, Map<String, CommandHandler> handlers)
			throws FileNotFoundException {
		this.modelFile = modelFile;
		this.tokenizer = new WavefrontTokenizer(new FileReader(modelFile));
		this.handlers = handlers;
	}

	private int nextToken() throws IOException, ParseException {
		while (true) {
			int token = tokenizer.nextToken();
			if (token != '\\') {
				return token;
			} else {
				if (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
					throwParseException();
				}
			}
		}
	}

	public abstract class CommandHandler {
		protected final void throwParseException() throws ParseException {
			WavefrontParser.this.throwParseException();
		}

		protected final float readFloat() throws IOException, ParseException {
			if (nextToken() != StreamTokenizer.TT_NUMBER) {
				throwParseException();
			}

			double value = tokenizer.nval;

			if ((tokenizer.nextToken() == StreamTokenizer.TT_WORD)
					&& tokenizer.sval.matches("e[+-][0-9]+")) {
				String exponent = tokenizer.sval.substring(1);
				value *= Math.pow(10, Double.valueOf(exponent));
			} else {
				tokenizer.pushBack();
			}

			return (float) value;
		}

		protected final boolean slash() throws IOException, ParseException {
			if (nextToken() != '/') {
				tokenizer.pushBack();
				return false;
			} else {
				return true;
			}
		}

		protected final boolean eol() throws IOException, ParseException {
			nextToken();
			tokenizer.pushBack();
			return tokenizer.ttype == StreamTokenizer.TT_EOL;
		}

		protected final File getReferencedFile(String fileName) {
			return new File(modelFile.getParentFile(), fileName);
		}

		public abstract void handle() throws IOException, ParseException;
	}

	public void skipLine() throws IOException, ParseException {
		while (nextToken() != StreamTokenizer.TT_EOL) {
		}
	}

	private void parseLine() throws IOException, ParseException {
		if (tokenizer.ttype == StreamTokenizer.TT_EOL) {
			return;
		}
		if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
			throwParseException();
		}

		if (handlers.containsKey(tokenizer.sval)) {
			handlers.get(tokenizer.sval).handle();
		} else {
			skipLine();
			return;
		}

		if (nextToken() != StreamTokenizer.TT_EOL) {
			throwParseException();
		}
	}

	public void parse() throws IOException, ParseException {
		while (nextToken() != StreamTokenizer.TT_EOF) {
			parseLine();
		}
	}

	public int readIndex(List<?> array) throws IOException, ParseException {
		if (nextToken() != StreamTokenizer.TT_NUMBER) {
			throwParseException();
		}

		int index = (int) tokenizer.nval;
		if ((index <= 0) || (index > array.size())) {
			throwParseException();
		}

		return index - 1;
	}

	public String readString() throws IOException, ParseException {
		if (nextToken() != StreamTokenizer.TT_WORD) {
			throwParseException();
		}
		return tokenizer.sval;
	}

	private void throwParseException() throws ParseException {
		throw new ParseException(tokenizer.lineno());
	}
}
