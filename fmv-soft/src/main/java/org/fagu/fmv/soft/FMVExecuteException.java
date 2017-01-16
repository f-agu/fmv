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

import org.apache.commons.exec.ExecuteException;


/**
 * @author f.agu
 */
public class FMVExecuteException extends ExecuteException {

	private static final long serialVersionUID = 1097668665104345846L;

	private final String commandLine;

	private final List<String> outputLines;

	/**
	 * @param executeException
	 * @param commandLine
	 */
	public FMVExecuteException(ExecuteException executeException, String commandLine, List<String> outputLines) {
		super(executeException.getMessage(), executeException.getExitValue());
		this.commandLine = commandLine;
		this.outputLines = Collections.unmodifiableList(outputLines);
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

}
