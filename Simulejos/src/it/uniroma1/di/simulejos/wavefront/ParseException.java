package it.uniroma1.di.simulejos.wavefront;

import java.io.File;

public class ParseException extends Exception {
	private static final long serialVersionUID = 6094056317169948299L;

	public final File sourceFile;
	public final int lineNumber;
	public final String message;

	ParseException(File sourceFile, int lineNumber, String message) {
		super(sourceFile.getAbsolutePath() + "(" + lineNumber + "): " + message);
		this.sourceFile = sourceFile;
		this.lineNumber = lineNumber;
		this.message = message;
	}
}
