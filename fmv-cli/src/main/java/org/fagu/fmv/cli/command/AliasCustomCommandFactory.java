package org.fagu.fmv.cli.command;

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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.cli.Command;
import org.fagu.fmv.cli.CommandBuilder;
import org.fagu.fmv.cli.CommandFactory;
import org.fagu.fmv.cli.exception.LineParseException;
import org.fagu.fmv.core.project.Project;

import jline.console.ConsoleReader;


/**
 * @author f.agu
 */
public class AliasCustomCommandFactory implements CommandFactory {

	private final String name;

	private final String line;

	/**
	 * @param name
	 * @param line
	 */
	public AliasCustomCommandFactory(String name, String line) {
		this.name = name;
		this.line = line;
	}

	/**
	 * @see org.fagu.fmv.cli.CommandFactory#getCommandName()
	 */
	@Override
	public String getCommandName() {
		return name;
	}

	/**
	 * @return the executable
	 */
	public String getLine() {
		return line;
	}

	/**
	 * @see org.fagu.fmv.cli.CommandFactory#create(jline.console.ConsoleReader, org.fagu.fmv.cli.CommandBuilder,
	 *      org.fagu.fmv.core.project.Project, java.lang.String, String[])
	 */
	@Override
	public Command create(final ConsoleReader consoleReader, final CommandBuilder commandBuilder, Project project, String executable,
			final String[] topArgs) throws LineParseException {
		return (Command)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {Command.class}, new InvocationHandler() {

			/**
			 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method,
			 *      java.lang.Object[])
			 */
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if("run".equals(method.getName())) {
					String ln = line;
					if(topArgs != null && topArgs.length > 0) {
						ln += " \"" + StringUtils.join(topArgs, "\" \"") + '"';
					}
					return commandBuilder.createAndExec(ln);
				}
				if("help".equals(method.getName())) {
					// TODO
				}
				throw new RuntimeException("Undefined process");
			}
		});
	}
}
