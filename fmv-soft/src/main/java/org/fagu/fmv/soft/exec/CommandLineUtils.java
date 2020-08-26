package org.fagu.fmv.soft.exec;

import java.util.Arrays;
import java.util.Collection;
import java.util.ServiceLoader;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.CommandLineToString.CommandLineToStringBuilder;


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
		CommandLineToStringBuilder builder = CommandLineToString.with(args)
				.whenArg().verify(s -> s.toLowerCase().startsWith("-pass")).hideNext();
		ServiceLoader.load(CommandLineHidePolicy.class).forEach(clhp -> clhp.applyPolicy(builder));
		return builder.build().toString();
	}

}
