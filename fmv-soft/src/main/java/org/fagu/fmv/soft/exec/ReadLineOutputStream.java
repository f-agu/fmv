package org.fagu.fmv.soft.exec;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.NullOutputStream;


/**
 * @author f.agu
 */
public class ReadLineOutputStream extends OutputStream {

	private final StringBuilder currentBuffer = new StringBuilder();

	private final ReadLine readLine;

	private final OutputStream outputStream;

	private long count;

	private boolean skipLF = false;

	/**
	 * @param readLine
	 */
	public ReadLineOutputStream(ReadLine readLine) {
		this(null, readLine);
	}

	/**
	 * @param outputStream
	 * @param readLine
	 */
	public ReadLineOutputStream(OutputStream outputStream, ReadLine readLine) {
		this.outputStream = outputStream != null ? outputStream : NullOutputStream.NULL_OUTPUT_STREAM;
		this.readLine = readLine;
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		++count;
		if(b == '\r') {
			skipLF = true;
			flushBuffer();
			return;
		}
		if(b == '\n') {
			if(skipLF) {
				skipLF = false;
				return;
			}
			flushBuffer();
			return;
		}
		currentBuffer.append((char)b);
		outputStream.write(b);
	}

	/**
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			outputStream.close();
		} finally {
			flushBuffer();
		}
	}

	// *********************************************

	/**
	 * 
	 */
	private void flushBuffer() {
		if(count > 0) {
			readLine.read(currentBuffer.toString());
			currentBuffer.setLength(0);
			count = 0;
		}
	}

}
