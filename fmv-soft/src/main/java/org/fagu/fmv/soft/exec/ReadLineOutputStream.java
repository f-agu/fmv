package org.fagu.fmv.soft.exec;

import java.io.ByteArrayOutputStream;
/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.output.NullOutputStream;


/**
 * @author f.agu
 */
public class ReadLineOutputStream extends OutputStream {

	private final ReadLine readLine;

	private final OutputStream outputStream;

	private final ByteArrayOutputStream byteArrayOutputStream;

	private final Charset charsets;

	private long count;

	private boolean skipLF = false;

	public ReadLineOutputStream(ReadLine readLine) {
		this(null, readLine, null);
	}

	public ReadLineOutputStream(OutputStream outputStream, ReadLine readLine) {
		this(outputStream, readLine, null);
	}

	public ReadLineOutputStream(OutputStream outputStream, ReadLine readLine, Charset charsets) {
		this.outputStream = outputStream != null ? outputStream : NullOutputStream.NULL_OUTPUT_STREAM;
		this.readLine = readLine;
		this.charsets = charsets != null ? charsets : Charset.defaultCharset();
		this.byteArrayOutputStream = new ByteArrayOutputStream(200);
	}

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
		byteArrayOutputStream.write(b);
	}

	@Override
	public void close() throws IOException {
		try {
			outputStream.close();
		} finally {
			flushBuffer();
		}
	}

	// *********************************************

	private void flushBuffer() {
		if(count > 0) {
			String s = new String(byteArrayOutputStream.toByteArray(), charsets);
			readLine.read(s);
			byteArrayOutputStream.reset();
			count = 0;
		}
	}

}
