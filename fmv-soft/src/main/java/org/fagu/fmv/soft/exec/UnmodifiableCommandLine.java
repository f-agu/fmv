package org.fagu.fmv.soft.exec;

import java.util.Map;

import org.apache.commons.exec.CommandLine;


/**
 * @author Utilisateur
 * @created 15 avr. 2019 10:15:33
 */
public class UnmodifiableCommandLine extends CommandLine {

	public UnmodifiableCommandLine(CommandLine commandLine) {
		super(commandLine);
	}

	@Override
	public CommandLine addArgument(String argument) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CommandLine addArgument(String argument, boolean handleQuoting) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CommandLine addArguments(String addArguments) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CommandLine addArguments(String addArguments, boolean handleQuoting) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CommandLine addArguments(String[] addArguments) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CommandLine addArguments(String[] addArguments, boolean handleQuoting) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSubstitutionMap(Map<String, ?> substitutionMap) {
		throw new UnsupportedOperationException();
	}

}
