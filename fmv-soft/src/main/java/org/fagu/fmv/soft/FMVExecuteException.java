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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.exec.exception.ExceptionKnown;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzers;


/**
 * @author f.agu
 */
public class FMVExecuteException extends ExecuteException {

	private static final long serialVersionUID = 1097668665104345846L;

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private final Class<? extends ExceptionKnownAnalyzer> exceptionKnownAnalyzerClass;

	private final String commandLine;

	private final List<String> outputLines;

	private Optional<ExceptionKnown> optionalExceptionKnown;

	/**
	 * @param exceptionKnownAnalyzerClass
	 * @param executeException
	 * @param commandLine
	 * @param outputLines
	 */
	public FMVExecuteException(Class<? extends ExceptionKnownAnalyzer> exceptionKnownAnalyzerClass, ExecuteException executeException,
			String commandLine, List<String> outputLines) {
		this(exceptionKnownAnalyzerClass, executeException.getExitValue(), executeException, commandLine, outputLines);
	}

	/**
	 * @param exceptionKnownAnalyzerClass
	 * @param exitValue
	 * @param exception
	 * @param commandLine
	 * @param outputLines
	 */
	public FMVExecuteException(Class<? extends ExceptionKnownAnalyzer> exceptionKnownAnalyzerClass, int exitValue, Exception exception,
			String commandLine, List<String> outputLines) {
		super(concat(exception.getMessage(), commandLine, outputLines), exitValue(exitValue, exception), exception);
		this.exceptionKnownAnalyzerClass = exceptionKnownAnalyzerClass;
		this.commandLine = Objects.requireNonNull(commandLine);
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

	/**
	 * @return
	 */
	public Optional<ExceptionKnown> getExceptionKnown() {
		if(optionalExceptionKnown == null && exceptionKnownAnalyzerClass != null) {
			optionalExceptionKnown = ExceptionKnownAnalyzers.getKnown(exceptionKnownAnalyzerClass, (Exception)getCause());
		}
		return optionalExceptionKnown;
	}

	/**
	 * @return
	 */
	public boolean isKnown() {
		return getExceptionKnown().isPresent();
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
		for(String str : outputLines) {
			sb.append(str).append(lineSeparator);
		}
		sb.append("nested exception is ").append(getCause());
		return sb.toString();
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

	/**
	 * @param exitValue
	 * @param exception
	 * @return
	 */
	private static int exitValue(int exitValue, Exception exception) {
		if(exception instanceof ExecuteException) {
			ExecuteException executeException = (ExecuteException)exception;
			return executeException.getExitValue();
		}
		return exitValue;
	}

}
