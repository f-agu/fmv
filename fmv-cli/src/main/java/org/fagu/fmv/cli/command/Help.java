package org.fagu.fmv.cli.command;

/*-
 * #%L
 * fmv-cli
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
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
		TreeMap<String, HelpTable> cmdFctMap = new TreeMap<>();

		for(CommandFactory commandFactory : commandBuilder.getCommandFactories()) {
			String commandName = commandFactory.getCommandName();
			if(org.fagu.fmv.cli.command.Alias.isAlias(commandFactory)) {
				String mainCmd = org.fagu.fmv.cli.command.Alias.getAliasCommand(commandFactory);
				cmdFctMap.computeIfAbsent(mainCmd, HelpTable::new).addAlias(commandName);
				continue;
			} else {
				HelpTable helpTable = cmdFctMap.computeIfAbsent(commandName, HelpTable::new);
				org.fagu.fmv.cli.Command cmd;
				try {
					cmd = commandFactory.create(consoleReader, commandBuilder, project, "", null);
				} catch(LineParseException e) {
					throw new RuntimeException(e);
				}
				helpTable.setDescription(cmd.getShortDescription());
			}

		}

		int maxLen = cmdFctMap.values().stream().mapToInt(ht -> ht.getNames().length()).max().orElse(0);

		for(HelpTable helpTable : cmdFctMap.values()) {
			StringBuilder buf = new StringBuilder(40);
			buf.append("  ").append(StringUtils.rightPad(helpTable.getNames(), maxLen)).append("  ").append(helpTable.description);
			println(buf.toString());
		}
	}

	// --------------------------------------------

	private static class HelpTable {

		private final String name;

		private final java.util.Set<String> aliases = new TreeSet<>();

		private String description;

		private HelpTable(String name) {
			this.name = name;
		}

		private void addAlias(String alias) {
			aliases.add(alias);
		}

		private void setDescription(String description) {
			this.description = description;
		}

		private String getNames() {
			StringBuilder buf = new StringBuilder(60);
			buf.append(name);
			for(String alias : aliases) {
				buf.append(", ").append(alias);
			}
			return buf.toString();
		}
	}
}
