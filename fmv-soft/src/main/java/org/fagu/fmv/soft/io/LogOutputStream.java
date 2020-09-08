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
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * @author f.agu
 * @created 8 juil. 2019 16:32:43
 */
public class LogOutputStream extends OutputStream {

	private final Consumer<String> logger;

	private final OutputStream outputStream;

	private final String hexCode;

	private long pointer;

	public LogOutputStream(OutputStream outputStream, Consumer<String> logger) {
		this.outputStream = Objects.requireNonNull(outputStream);
		this.logger = Objects.requireNonNull(logger);
		this.hexCode = Log.reference(this);
	}

	@Override
	public void write(int b) throws IOException {
		logger.accept(pointerToString() + "write(" + Log.data(b) + ")");
		outputStream.write(b);
		++pointer;
	}

	@Override
	public void write(byte[] b) throws IOException {
		logger.accept(pointerToString() + "write(byte[" + b.length + "]) " + Log.data(b, 0, b.length));
		outputStream.write(b);
		pointer += b.length;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		logger.accept(pointerToString() + "write(byte[], " + off + ", " + len + ") " + Log.data(b, off, len));
		outputStream.write(b, off, len);
		pointer += len;
	}

	@Override
	public void flush() throws IOException {
		logger.accept(pointerToString() + "flush()");
		outputStream.flush();
	}

	@Override
	public void close() throws IOException {
		logger.accept(pointerToString() + "close()");
		outputStream.close();
	}
	// ********************************

	private String pointerToString() {
		return hexCode + " (@" + pointer + " / 0x" + Long.toHexString(pointer) + ") ";
	}

}
