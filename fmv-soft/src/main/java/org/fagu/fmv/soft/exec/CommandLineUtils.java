package org.fagu.fmv.soft.exec;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.exec.CommandLine;


/**
 * @author f.agu
 */
public class CommandLineUtils {

	private CommandLineUtils() {}

	public static String toLine(CommandLine commandLine) {
		return toLine(commandLine.toStrings());
	}

	public static String toLine(String... args) {
		return toLine(Arrays.asList(args));
	}

	public static String toLine(Collection<String> args) {
		return CommandLineToString.with(args)
				.whenArg().verify(s -> s.toLowerCase().startsWith("-pass")).hideNext()
				.build().toString();
	}

}
