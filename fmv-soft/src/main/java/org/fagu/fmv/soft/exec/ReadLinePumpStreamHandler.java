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


/**
 * @author f.agu
 */
public class ReadLinePumpStreamHandler extends WritablePumpStreamHandler {

	private final ReadLine outReadLine;

	private final ReadLine errReadLine;

	/**
	 * @param outAndErrReadLine
	 */
	public ReadLinePumpStreamHandler(ReadLine outAndErrReadLine) {
		this(outAndErrReadLine, outAndErrReadLine);
	}

	/**
	 * @param outReadLine
	 * @param errReadLine
	 */
	public ReadLinePumpStreamHandler(ReadLine outReadLine, ReadLine errReadLine) {
		this.outReadLine = outReadLine;
		this.errReadLine = errReadLine;
	}

	/**
	 * @see org.apache.commons.exec.PumpStreamHandler#setProcessOutputStream(java.io.InputStream)
	 */
	@Override
	public void setProcessOutputStream(InputStream is) {
		createProcessOutputPump(new MyInputStream(is, outReadLine), null);
	}

	/**
	 * @see org.apache.commons.exec.PumpStreamHandler#setProcessErrorStream(java.io.InputStream)
	 */
	@Override
	public void setProcessErrorStream(InputStream is) {
		createProcessErrorPump(new MyInputStream(is, errReadLine), null);
	}

	// *************************************************

	/**
	 * @see org.apache.commons.exec.PumpStreamHandler#createPump(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	protected Thread createPump(InputStream is, OutputStream os) {
		MyInputStream myInputStream = (MyInputStream)is;
		return createPump(myInputStream.delegate, myInputStream.readLine);
	}

	/**
	 * @param is
	 * @param readLine
	 * @return
	 */
	protected Thread createPump(InputStream is, ReadLine readLine) {
		final Thread result = new Thread(new ReadLineStreamPumper(is, readLine), "ReadLineStreamPumper");
		result.setDaemon(true);
		return result;
	}

	// -------------------------------------

	/**
	 * @author f.agu
	 */
	private class MyInputStream extends InputStream {

		private final ReadLine readLine;

		private final InputStream delegate;

		/**
		 * @param delegate
		 * @param readLine
		 */
		MyInputStream(InputStream delegate, ReadLine readLine) {
			this.delegate = delegate;
			this.readLine = readLine;
		}

		/**
		 * @see java.io.InputStream#read()
		 */
		@Override
		public int read() throws IOException {
			return 0;
		}
	}

}
