package lejos.internal.io;


import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lejos.internal.charset.CharsetEncoder;

/**
 * Abstract Single Byte Character Set OutputStream Writer.
 * @author Sven Köhler
 */
public class LejosOutputStreamWriter extends Writer
{
	private final static int MIN_BUFFERSIZE = 16;
	
	private final CharsetEncoder coder;
	private final OutputStream os;
	private final byte[] buffer;
	private final int limit;
	//cache for storing a high surrogate
	private char high;
	
	public LejosOutputStreamWriter(OutputStream os, CharsetEncoder coder, int buffersize)
	{
		this.os = os;
		this.coder = coder;
		
		if (buffersize < MIN_BUFFERSIZE)
			buffersize = MIN_BUFFERSIZE;
				
		if (coder.getMaxCharLength() > buffersize)
			throw new IllegalArgumentException("buffer to small for given charset");
		
		this.buffer = new byte[buffersize];
		this.limit = buffersize - coder.getMaxCharLength();
	}
	
	private int writeChar(int len, char c) throws IOException
	{
		if (Character.isHighSurrogate(c))		
		{
			if (this.high > 0)
				len = this.coder.encode(-1, buffer, len);
			
			this.high = c;
			return len;
		}
		
		int cp;
		if (!Character.isLowSurrogate(c))
			cp = c;
		else
		{
			if (this.high == 0)
				return this.coder.encode(-1, buffer, len);
			
			cp = Character.toCodePoint(high, c);
			this.high = 0;
		}
		
		if (len >= limit)
		{
			this.bufferFlush(len);
			len = 0;
		}
		
		return this.coder.encode(cp, this.buffer, len);
	}
	
	private void bufferFlush(int len) throws IOException
	{
		this.os.write(this.buffer, 0, len);
	}
	
	
	@Override
	public Writer append(char c) throws IOException
	{
		this.bufferFlush(this.writeChar(0, c));
		return this;
	}

	@Override
	public Writer append(CharSequence str, int start, int end) throws IOException
	{
		int bl = 0;
		for (int i=start; i<end; i++)
			bl = this.writeChar(bl, str.charAt(i));
		
		this.bufferFlush(bl);
		return this;
	}

	@Override
	public void write(int c) throws IOException
	{
		this.bufferFlush(this.writeChar(0, (char)c));
	}

	@Override
	public void write(String str, int off, int len) throws IOException
	{
		int bl = 0;
		int end = off + len;
		for (int i=off; i<end; i++)
			bl = this.writeChar(bl, str.charAt(i));
			
		this.bufferFlush(bl);
	}

	@Override
	public void write(char[] c, int off, int len) throws IOException
	{
		int bl = 0;
		int end = off + len;
		for (int i=off; i<end; i++)
			bl = this.writeChar(bl, c[i]);
		
		this.bufferFlush(bl);
	}

	@Override
	public void close() throws IOException
	{
		if (this.high > 0)
		{
			this.high = 0;
			this.bufferFlush(this.coder.encode(-1, buffer, 0));
		}
		
		this.os.close();
	}

	@Override
	public void flush() throws IOException
	{
		this.os.flush();
	}
}
