package org.fagu.fmv.cli.utils;

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

import jline.console.ConsoleReader;


/**
 * @author f.agu
 */
public class Printer {

	protected ConsoleReader consoleReader;

	/**
	 * @param consoleReader
	 */
	public Printer(ConsoleReader consoleReader) {
		this.consoleReader = consoleReader;
	}

	/**
	 * @param msg
	 */
	public void print(String msg) {
		try {
			consoleReader.print(msg);
			consoleReader.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param msg
	 */
	public void println(String msg) {
		try {
			consoleReader.println(msg);
			consoleReader.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
