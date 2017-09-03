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
import java.util.concurrent.Future;


/**
 * @author f.agu
 */
public class FMVFuture<V> extends UnaryWrapFuture<V> {

	private final OutputStream processInputStream;

	/**
	 * @param delegated
	 * @param processInputStream
	 */
	FMVFuture(Future<V> delegated, OutputStream processInputStream) {
		super(delegated);
		this.processInputStream = processInputStream;
	}

	/**
	 * @param b
	 * @throws IOException
	 */
	public void write(int b) throws IOException {
		if(processInputStream != null) {
			processInputStream.write(b);
		}
	}

	/**
	 * @param b
	 * @throws IOException
	 */
	public void write(byte b[]) throws IOException {
		if(processInputStream != null) {
			processInputStream.write(b);
		}
	}

	/**
	 * @param b
	 * @param off
	 * @param len
	 * @throws IOException
	 */
	public void write(byte b[], int off, int len) throws IOException {
		if(processInputStream != null) {
			processInputStream.write(b, 0, b.length);
		}
	}

}
