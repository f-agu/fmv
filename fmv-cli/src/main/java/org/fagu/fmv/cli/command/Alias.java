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

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.cli.CommandBuilder;
import org.fagu.fmv.cli.CommandFactory;
import org.fagu.fmv.cli.annotation.Command;


/**
 * @author f.agu
 */
@Command("alias")
public class Alias extends AbstractCommand {

	// autowired
	private CommandBuilder commandBuilder;

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		if(args.length == 0) { // list
			for(CommandFactory commandFactory : commandBuilder.getCommandFactories()) {
				if(isAlias(commandFactory)) {
					AliasCustomCommandFactory aliasCCF = (AliasCustomCommandFactory)commandFactory;
					StringBuilder buf = new StringBuilder(20);
					buf.append("alias ").append(aliasCCF.getCommandName()).append("='").append(aliasCCF.getLine()).append('\'');
					println(buf.toString());
				}
			}
			return;
		}

		String line = StringUtils.join(args, ' ');
		int pos = line.indexOf('=');
		if(pos < 1) {
			// TODO display();
			System.out.println("beurk !");
			return;
		}
		String varName = line.substring(0, pos).trim();
		String varValue = line.substring(pos + 1).trim();

		varValue = StringUtils.strip(varValue, "\"");

		commandBuilder.addAlias(varName, varValue);
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "Create an alias or list all alias";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "alias [name[=value] ...]";
	}

	/**
	 * @param commandFactory
	 */
	public static boolean isAlias(CommandFactory commandFactory) {
		return commandFactory instanceof AliasCustomCommandFactory;
	}

	/**
	 * @param commandFactory
	 * @return
	 */
	public static String getAliasCommand(CommandFactory commandFactory) {
		AliasCustomCommandFactory accf = (AliasCustomCommandFactory)commandFactory;
		return accf.getLine();
	}

}
