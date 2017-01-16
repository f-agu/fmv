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
import java.io.OutputStream;

import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.NullOutputStream;


/**
 * @author f.agu
 */
public class WritablePumpStreamHandler extends PumpStreamHandler {

	private OutputStream outputStream;

	/**
	 * 
	 */
	public WritablePumpStreamHandler() {
		super(NullOutputStream.NULL_OUTPUT_STREAM);
	}

	/**
	 * @see org.apache.commons.exec.PumpStreamHandler#setProcessInputStream(java.io.OutputStream)
	 */
	@Override
	public void setProcessInputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * @param b
	 * @throws IOException
	 */
	public void write(int b) throws IOException {
		outputStream.write(b);
	}

	/**
	 * @param b
	 * @throws IOException
	 */
	public void write(byte b[]) throws IOException {
		outputStream.write(b);
	}

	/**
	 * @param b
	 * @param off
	 * @param len
	 * @throws IOException
	 */
	public void write(byte b[], int off, int len) throws IOException {
		outputStream.write(b, 0, b.length);
	}

}
