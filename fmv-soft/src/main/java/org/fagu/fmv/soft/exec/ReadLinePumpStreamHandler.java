package org.fagu.fmv.soft.exec;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 fagu
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
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.fagu.fmv.soft.io.StreamLog;
import org.fagu.fmv.soft.io.StreamLogConsumer;


/**
 * @author f.agu
 */
public class ReadLinePumpStreamHandler extends WritablePumpStreamHandler {

	private final ReadLine outReadLine;

	private final ReadLine errReadLine;

	private final Charset charset;

	private final LookReader lookReader;

	public ReadLinePumpStreamHandler(ReadLine outAndErrReadLine, Charset charset) {
		this(outAndErrReadLine, outAndErrReadLine, charset);
	}

	public ReadLinePumpStreamHandler(ReadLine outReadLine, ReadLine errReadLine, Charset charset) {
		this(outReadLine, errReadLine, charset, null);
	}

	public ReadLinePumpStreamHandler(ReadLine outReadLine, ReadLine errReadLine, Charset charset, LookReader lookReader) {
		this.outReadLine = outReadLine;
		this.errReadLine = errReadLine;
		this.charset = charset;
		this.lookReader = lookReader;
	}

	@Override
	public void setProcessOutputStream(InputStream is) {
		createProcessOutputPump(new ReadLineInputStream(StreamLog.wrap(is, StreamLogConsumer.out()), outReadLine), null);
	}

	@Override
	public void setProcessErrorStream(InputStream is) {
		createProcessErrorPump(new ReadLineInputStream(StreamLog.wrap(is, StreamLogConsumer.err()), errReadLine), null);
	}

	public Charset getCharset() {
		return charset;
	}

	// *************************************************

	@Override
	protected Thread createPump(InputStream is, OutputStream os) {
		ReadLineInputStream myInputStream = (ReadLineInputStream)is;
		return createPump(myInputStream.delegate, myInputStream.readLine);
	}

	protected Thread createPump(InputStream is, ReadLine readLine) {
		final Thread result = new Thread(new ReadLineStreamPumper(is, readLine, charset, lookReader), "ReadLineStreamPumper");
		result.setDaemon(true);
		return result;
	}

	@Override
	protected Thread createPump(InputStream is, OutputStream os, boolean closeWhenExhausted) {
		return super.createPump(is, os, false);
	}

	// -------------------------------------

	/**
	 * @author f.agu
	 */
	public static class ReadLineInputStream extends InputStream {

		private final ReadLine readLine;

		private final InputStream delegate;

		ReadLineInputStream(InputStream delegate, ReadLine readLine) {
			this.delegate = delegate;
			this.readLine = readLine != null ? readLine : l -> {};
		}

		@Override
		public int read() throws IOException {
			return 0;
		}
	}

}
