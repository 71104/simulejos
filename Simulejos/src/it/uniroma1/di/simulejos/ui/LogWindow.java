package it.uniroma1.di.simulejos.ui;

import java.awt.Font;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JTextArea;

final class LogWindow extends JTextArea {
	private static final long serialVersionUID = -928033214250978702L;

	{
		setEditable(false);
		setRows(6);
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
	}

	public Writer getWriter() {
		return new Writer() {
			private boolean closed;

			@Override
			public synchronized void write(char[] buffer, int offset, int length) {
				if (!closed) {
					LogWindow.this.append(new String(buffer).substring(offset,
							offset + length));
				}
			}

			@Override
			public synchronized void flush() throws IOException {
			}

			@Override
			public synchronized void close() throws IOException {
				closed = true;
			}
		};
	}
}
