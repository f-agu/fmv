package org.fagu.fmv.soft.io;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * @author f.agu
 * @created 8 juil. 2019 16:32:38
 */
public class LogInputStream extends InputStream {

	private final Consumer<String> logger;

	private final InputStream inputStream;

	private final String hexCode;

	private int markPos = - 1;

	private long pointer;

	public LogInputStream(InputStream inputStream, Consumer<String> logger) {
		this.inputStream = Objects.requireNonNull(inputStream);
		this.logger = Objects.requireNonNull(logger);
		this.hexCode = Log.reference(this);
	}

	@Override
	public int read() throws IOException {
		int read = inputStream.read();
		logger.accept(pointerToString() + "read(): " + (read < 0 ? read : Log.data(read)));
		if(read >= 0) {
			++pointer;
		}
		return read;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read = inputStream.read(b);
		logger.accept(pointerToString() + "read(byte[]): " + (read < 0 ? read : Log.data(b, 0, read)));
		if(read >= 0) {
			pointer += read;
		}
		return read;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = inputStream.read(b, off, len);
		logger.accept(pointerToString() + "read(byte[], " + off + ", " + len + "): " + (read < 0 ? read : Log.data(b, off, read)));
		if(read >= 0) {
			pointer += read;
		}
		return read;
	}

	@Override
	public int available() throws IOException {
		int available = inputStream.available();
		logger.accept(pointerToString() + "available(): " + available);
		return available;
	}

	@Override
	public long skip(long n) throws IOException {
		long skip = inputStream.skip(n);
		logger.accept(pointerToString() + "skip(" + n + "): " + skip + " -> @ " + (pointer + skip));
		pointer += skip;
		return skip;
	}

	@Override
	public boolean markSupported() {
		boolean markSupported = inputStream.markSupported();
		logger.accept(pointerToString() + "markSupported(): " + markSupported);
		return markSupported;
	}

	@Override
	public synchronized void mark(int readlimit) {
		logger.accept(pointerToString() + "mark(" + readlimit + ")");
		inputStream.mark(readlimit);
		if(inputStream.markSupported()) {
			markPos = readlimit;
		}
	}

	@Override
	public synchronized void reset() throws IOException {
		logger.accept(pointerToString() + "reset()");
		inputStream.reset();
		if(inputStream.markSupported() && markPos > 0) {
			pointer = markPos;
		}
	}

	@Override
	public void close() throws IOException {
		logger.accept(pointerToString() + "close()");
		inputStream.close();
	}

	// ********************************

	private String pointerToString() {
		return hexCode + " (@" + pointer + " / 0x" + Long.toHexString(pointer) + ") ";
	}

}
