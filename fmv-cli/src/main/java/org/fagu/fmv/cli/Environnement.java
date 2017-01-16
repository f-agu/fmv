package org.fagu.fmv.cli;

/*
 * #%L
 * fmv-cli
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import org.fagu.fmv.core.exec.Executable;
import org.fagu.fmv.core.project.Project;


/**
 * @author f.agu
 */
public class Environnement {

	private FMVCLIConfig fmvcliConfig;

	private CommandBuilder commandBuilder;

	private Project project;

	private ConsoleReader consoleReader;

	private Prompt prompt;

	private Executable currentExecutable;

	/**
	 * @param project
	 * @param fmvcliConfig
	 */
	public Environnement(Project project, FMVCLIConfig fmvcliConfig) {
		this(project, fmvcliConfig, null, null);
	}

	/**
	 * @param project
	 * @param fmvcliConfig
	 * @param prompt
	 * @param commandBuilder
	 */
	public Environnement(Project project, FMVCLIConfig fmvcliConfig, Prompt prompt, CommandBuilder commandBuilder) {
		this.project = project;
		this.fmvcliConfig = fmvcliConfig;
		this.prompt = prompt != null ? prompt : new BasePrompt();
		if(commandBuilder != null) {
			this.commandBuilder = commandBuilder;
		} else {
			try {
				this.consoleReader = new ConsoleReader();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			this.commandBuilder = new DefaultCommandBuilder(this);
		}
	}

	/**
	 * @return the currentExecutable
	 */
	public Executable getCurrentExecutable() {
		return currentExecutable;
	}

	/**
	 * @param currentExecutable the currentExecutable to set
	 */
	public void setCurrentExecutable(Executable currentExecutable) {
		this.currentExecutable = currentExecutable;
	}

	/**
	 * @return the fmvcliConfig
	 */
	public FMVCLIConfig getFMVCLIConfig() {
		return fmvcliConfig;
	}

	/**
	 * @return the commandBuilder
	 */
	public CommandBuilder getCommandBuilder() {
		return commandBuilder;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @return the consoleReader
	 */
	public ConsoleReader getConsoleReader() {
		return consoleReader;
	}

	/**
	 * @return the prompt
	 */
	public Prompt getPrompt() {
		return prompt;
	}
}
