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

import jline.console.ConsoleReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fagu.fmv.cli.Command;
import org.fagu.fmv.cli.Environnement;
import org.fagu.fmv.cli.FMVCLIConfig;
import org.fagu.fmv.cli.FMVHelpFormatter;
import org.fagu.fmv.cli.utils.Printer;
import org.fagu.fmv.core.project.Project;


/**
 * @author f.agu
 */
public abstract class AbstractCommand implements Command {

	// autowired
	protected Environnement environnement;

	// autowired
	protected FMVCLIConfig fmvCliConfig;

	// autowired
	protected Project project;

	// autowired
	protected ConsoleReader consoleReader;

	private Printer printer;

	@Override
	public Options getOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, "");
		return options;
	}

	@Override
	public void help() {
		getHelpFormatter().printHelp(getSyntax(), getOptions());
	}

	// *******************************************

	protected CommandLine parse(String[] args) {
		Options options = getOptions();
		if(options == null) {
			throw new NullPointerException();
		}
		CommandLineParser parser = new GnuParser();
		try {
			return parser.parse(options, args);
		} catch(ParseException e) {
			println(e.getMessage());
			println(getSyntax());
		}
		return null;
	}

	protected void print(String msg) {
		getPrinter().print(msg);
	}

	protected void println(String msg) {
		getPrinter().println(msg);
	}

	protected Printer getPrinter() {
		if(printer == null) {
			printer = new Printer(consoleReader);
		}
		return printer;
	}

	protected HelpFormatter getHelpFormatter() {
		return new FMVHelpFormatter(consoleReader);
	}

}
