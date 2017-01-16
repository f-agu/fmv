package org.fagu.fmv.cli;

/*
 * #%L
 * fmv-cli
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
import java.io.PrintWriter;
import java.util.Objects;

import jline.console.ConsoleReader;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.output.NullOutputStream;


/**
 * @author f.agu
 */
public class FMVHelpFormatter extends HelpFormatter {

	private final ConsoleReader consoleReader;

	/**
	 * @param consoleReader
	 */
	public FMVHelpFormatter(ConsoleReader consoleReader) {
		this.consoleReader = Objects.requireNonNull(consoleReader);
	}

	/**
	 * @see org.apache.commons.cli.HelpFormatter#printWrapped(java.io.PrintWriter, int, int, java.lang.String)
	 */
	@Override
	public void printWrapped(PrintWriter pw, int width, int nextLineTabStop, String text) {
		super.printWrapped(getPrintWriter(), width, nextLineTabStop, text);
	}

	/**
	 * @see org.apache.commons.cli.HelpFormatter#printOptions(java.io.PrintWriter, int, org.apache.commons.cli.Options,
	 *      int, int)
	 */
	@Override
	public void printOptions(PrintWriter pw, int width, Options options, int leftPad, int descPad) {
		super.printOptions(getPrintWriter(), width, options, leftPad, descPad);
	}

	// *****************************************

	/**
	 * @return
	 */
	private PrintWriter getPrintWriter() {
		return new PrintWriter(NullOutputStream.NULL_OUTPUT_STREAM) {

			/**
			 * @see java.io.PrintWriter#println(java.lang.String)
			 */
			@Override
			public void println(String x) {
				try {
					consoleReader.println(x);
					consoleReader.flush();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		};
	}

}
