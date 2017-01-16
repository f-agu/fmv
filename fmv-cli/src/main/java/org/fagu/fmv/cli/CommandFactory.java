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


import jline.console.ConsoleReader;

import org.fagu.fmv.cli.exception.LineParseException;
import org.fagu.fmv.core.project.Project;


/**
 * @author f.agu
 */
public interface CommandFactory {

	/**
	 * @return
	 */
	String getCommandName();

	/**
	 * @param consoleReader
	 * @param commandBuilder
	 * @param project
	 * @param executable
	 * @param args TODO
	 * @return
	 * @throws LineParseException
	 */
	Command create(ConsoleReader consoleReader, CommandBuilder commandBuilder, Project project, String executable, String[] args) throws LineParseException;
}
