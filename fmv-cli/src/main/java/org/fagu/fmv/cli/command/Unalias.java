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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.fagu.fmv.cli.CommandBuilder;
import org.fagu.fmv.cli.CommandFactory;
import org.fagu.fmv.cli.annotation.Command;


/**
 * @author f.agu
 */
@Command("unalias")
public class Unalias extends AbstractCommand {

	// autowired
	private CommandBuilder commandBuilder;

	/**
	 * @see org.fagu.fmv.cli.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] args) {
		CommandLine cmdLine = parse(args);

		List<String> names;
		if(cmdLine.hasOption('a')) {
			names = new ArrayList<>();
			for(CommandFactory commandFactory : commandBuilder.getCommandFactories()) {
				if(commandFactory instanceof AliasCustomCommandFactory) {
					names.add(commandFactory.getCommandName());
				}
			}
		} else {
			names = Arrays.asList(args);
		}

		for(String name : names) {
			commandBuilder.remove(name);
		}
	}

	/**
	 * @see org.fagu.fmv.cli.command.AbstractCommand#getOptions()
	 */
	@Override
	public Options getOptions() {
		Options options = new Options();
		options.addOption("a", "all", false, "remove all alias");
		return options;
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getShortDescription()
	 */
	@Override
	public String getShortDescription() {
		return "Remove alias definitions";
	}

	/**
	 * @see org.fagu.fmv.cli.Command#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "unlias [-a] name [name ...]";
	}

}
