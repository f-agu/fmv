package org.fagu.fmv.mymedia.utils;

/*-
 * #%L
 * fmv-mymedia
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;


/**
 * @author f.agu
 * @created 13 mai 2018 14:43:59
 */
public class UnclosedPrintStream extends PrintStream {

	/**
	 * @param out
	 */
	public UnclosedPrintStream(OutputStream out) {
		super(out);
	}

	/**
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public UnclosedPrintStream(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	/**
	 * @param file
	 * @throws FileNotFoundException
	 */
	public UnclosedPrintStream(File file) throws FileNotFoundException {
		super(file);
	}

	/**
	 * @param out
	 * @param autoFlush
	 */
	public UnclosedPrintStream(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	/**
	 * @param fileName
	 * @param csn
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public UnclosedPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	/**
	 * @param file
	 * @param csn
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public UnclosedPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
	}

	/**
	 * @param out
	 * @param autoFlush
	 * @param encoding
	 * @throws UnsupportedEncodingException
	 */
	public UnclosedPrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
	}

	/**
	 * @see java.io.PrintStream#close()
	 */
	@Override
	public void close() {
		super.flush();
	}

}
