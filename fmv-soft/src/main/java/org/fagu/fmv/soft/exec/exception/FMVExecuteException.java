package org.fagu.fmv.soft.exec.exception;

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
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class FMVExecuteException extends ExecuteException {

	private static final long serialVersionUID = 1097668665104345846L;

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private final Class<? extends ExceptionKnownAnalyzer> exceptionKnownAnalyzerClass;

	private final String commandLine;

	private final List<String> outputLines;

	private ExceptionKnown exceptionKnown;

	public FMVExecuteException(Class<? extends ExceptionKnownAnalyzer> exceptionKnownAnalyzerClass, ExecuteException executeException,
			String commandLine, List<String> outputLines) {
		this(exceptionKnownAnalyzerClass, executeException.getExitValue(), executeException, commandLine, outputLines);
	}

	public FMVExecuteException(Class<? extends ExceptionKnownAnalyzer> exceptionKnownAnalyzerClass, int exitValue, Exception exception,
			String commandLine, List<String> outputLines) {
		super(concat(exception.getMessage(), commandLine, outputLines), exitValue(exitValue, exception), exception);
		this.exceptionKnownAnalyzerClass = exceptionKnownAnalyzerClass;
		this.commandLine = Objects.requireNonNull(commandLine);
		this.outputLines = outputLines != null ? Collections.unmodifiableList(outputLines) : null;
	}

	public String getCommandLine() {
		return commandLine;
	}

	public List<String> getOutputLines() {
		return outputLines;
	}

	public ExceptionKnown getExceptionKnown() {
		if(exceptionKnown == null && exceptionKnownAnalyzerClass != null) {
			exceptionKnown = ExceptionKnownAnalyzers.getKnown(exceptionKnownAnalyzerClass, this).orElse(null);
		}
		return exceptionKnown;
	}

	public boolean isKnown() {
		return getExceptionKnown() != null;
	}

	// *****************************************************

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

	private static int exitValue(int exitValue, Exception exception) {
		if(exception instanceof ExecuteException) {
			ExecuteException executeException = (ExecuteException)exception;
			return executeException.getExitValue();
		}
		return exitValue;
	}

}
