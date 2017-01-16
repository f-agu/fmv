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

import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.fagu.fmv.cli.CommandBuilder;
import org.fagu.fmv.cli.CommandFactory;
import org.fagu.fmv.cli.annotation.Alias;
import org.fagu.fmv.cli.annotation.Command;
import org.fagu.fmv.cli.exception.LineParseException;


/**
 * @author f.agu
 */
@Command("help")
@Alias("?")
public class Help extends AbstractCommand {

	// autowired
	private CommandBuilder commandBuilder;

	/**
	 *
	 */
	public Help() {}

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length == 0) {
			listAllCommands();
			return;
		}
		for(String arg : args) {
			CommandFactory commandFactory = commandBuilder.getCommandFactory(arg);
			if(commandFactory == null) {
				println("Command not found: " + arg);
				continue;
			}
			org.fagu.fmv.cli.Command command = null;
			try {
				command = commandFactory.create(consoleReader, commandBuilder, project, arg, args);
			} catch(LineParseException e) {
				continue;
			}
			command.help();
		}
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "Help !";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "help <command> [<command> ...]";
	}

	// *****************************************

	/**
	 *
	 */
	protected void listAllCommands() {
		TreeMap<String, String> cmdFctMap = new TreeMap<String, String>();
		int maxLen = 0;
		for(CommandFactory commandFactory : commandBuilder.getCommandFactories()) {
			String commandName = commandFactory.getCommandName();
			String text = null;
			if(org.fagu.fmv.cli.command.Alias.isAlias(commandFactory)) {
				text = "<alias> " + org.fagu.fmv.cli.command.Alias.getAliasCommand(commandFactory);
			} else {
				org.fagu.fmv.cli.Command cmd;
				try {
					cmd = commandFactory.create(consoleReader, commandBuilder, project, "", null);
				} catch(LineParseException e) {
					throw new RuntimeException(e);
				}
				text = cmd.getShortDescription();
			}

			cmdFctMap.put(commandName, text);
			maxLen = Math.max(maxLen, commandName.length());
		}

		for(Entry<String, String> entry : cmdFctMap.entrySet()) {
			StringBuilder buf = new StringBuilder(40);
			buf.append("  ").append(StringUtils.rightPad(entry.getKey(), maxLen)).append("  ").append(entry.getValue());
			println(buf.toString());
		}
	}
}
