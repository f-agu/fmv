package org.fagu.fmv.soft;

/*
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class FMVExecuteException extends ExecuteException {

	private static final long serialVersionUID = 1097668665104345846L;

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private final String commandLine;

	private final List<String> outputLines;

	/**
	 * @param executeException
	 * @param commandLine
	 * @param outputLines
	 */
	public FMVExecuteException(ExecuteException executeException, String commandLine, List<String> outputLines) {
		super(concat(executeException.getMessage(), commandLine, outputLines), executeException.getExitValue(), executeException);
		this.commandLine = commandLine;
		this.outputLines = outputLines != null ? Collections.unmodifiableList(outputLines) : null;
	}

	/**
	 * @return
	 */
	public String getCommandLine() {
		return commandLine;
	}

	/**
	 * @return
	 */
	public List<String> getOutputLines() {
		return outputLines;
	}

	// *****************************************************

	/**
	 * @param message
	 * @param commandLine
	 * @param outputLines
	 * @return
	 */
	private static String concat(String message, String commandLine, List<String> outputLines) {
		StringBuilder msg = new StringBuilder(message);
		if(StringUtils.isNotEmpty(commandLine)) {
			msg.append(LINE_SEPARATOR).append(commandLine);
		}
		if(outputLines != null && ! outputLines.isEmpty()) {
			msg.append(LINE_SEPARATOR).append(outputLines.stream().collect(Collectors.joining(LINE_SEPARATOR)));
		}
		return msg.toString();
	}

}
