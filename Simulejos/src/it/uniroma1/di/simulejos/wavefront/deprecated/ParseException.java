package it.uniroma1.di.simulejos.wavefront.deprecated;

import java.io.IOException;

public final class ParseException extends IOException {
	private static final long serialVersionUID = 2774249187603527409L;

	private final int lineNumber;

	public ParseException(int lineNumber) {
		super("syntax error at line " + lineNumber);
		this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}
