package org.fagu.fmv.ffmpeg.exception;

/*
 * #%L
 * fmv-ffmpeg
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
import java.util.Collections;
import java.util.List;


/**
 * @author f.agu
 */
public class ExecuteIOException extends IOException {

	private static final long serialVersionUID = - 2015727050244822708L;

	private final String commandLine;

	private final List<String> outputs;

	/**
	 * @param cause
	 * @param commandLine
	 */
	public ExecuteIOException(IOException cause, String commandLine) {
		this(cause, commandLine, (List<String>)null);
	}

	/**
	 * @param cause
	 * @param commandLine
	 * @param output
	 */
	public ExecuteIOException(IOException cause, String commandLine, String output) {
		this(cause, commandLine, Collections.singletonList(output));
	}

	/**
	 * @param cause
	 * @param commandLine
	 * @param outputs
	 */
	public ExecuteIOException(IOException cause, String commandLine, List<String> outputs) {
		super(cause);
		this.commandLine = commandLine;
		this.outputs = outputs;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		String lineSeparator = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder(200);
		sb.append(super.getMessage()).append(lineSeparator);
		sb.append(commandLine).append(lineSeparator);
		for(String str : outputs) {
			sb.append(str).append(lineSeparator);
		}
		sb.append("nested exception is ").append(getCause());
		return sb.toString();
	}

}
