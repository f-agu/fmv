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


import java.util.Collection;

import org.fagu.fmv.cli.exception.LineParseException;


/**
 * @author f.agu
 */
public interface CommandBuilder {

	/**
	 * @param cmdClass
	 */
	void add(Class<? extends Command> cmdClass);

	/**
	 * @param pckg
	 */
	void add(Package pckg);

	/**
	 * @param packageName
	 */
	void add(String packageName);

	/**
	 * @param commandFactory
	 */
	void add(CommandFactory commandFactory);

	/**
	 * @param name
	 * @param line
	 */
	void addAlias(String name, String line);

	/**
	 * @param name
	 * @return
	 */
	CommandFactory getCommandFactory(String name);

	/**
	 * @return
	 */
	Collection<CommandFactory> getCommandFactories();

	/**
	 * @param name
	 * @return
	 */
	CommandFactory remove(String name);

	/**
	 * @param line
	 * @return
	 * @throws LineParseException
	 */
	Command createAndExec(String line) throws LineParseException;
}
