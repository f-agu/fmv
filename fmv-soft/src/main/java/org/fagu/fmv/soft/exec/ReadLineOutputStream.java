package org.fagu.fmv.soft.exec;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.NullOutputStream;


/**
 * @author f.agu
 */
public class ReadLineOutputStream extends OutputStream {

	private final StringBuilder buffer = new StringBuilder();

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
		outputStream.write(b);
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
		buffer.append((char)b);
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
			readLine.read(buffer.toString());
			buffer.setLength(0);
			count = 0;
		}
	}

}
