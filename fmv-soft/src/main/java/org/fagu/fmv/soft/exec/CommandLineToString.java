package org.fagu.fmv.soft.exec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 * @created 14 avr. 2019 16:19:26
 */
public class CommandLineToString {

	public static final String HIDE = "*******";

	// ------------------------------------------------------------

	public static class CommandLineToStringBuilder {

		private final Collection<String> args;

		private final List<ArgsFunction> argsFunctions;

		private CommandLineToStringBuilder(Collection<String> args) {
			this.args = Collections.unmodifiableList(new ArrayList<>(args));
			argsFunctions = new ArrayList<>();
		}

		public WhenArgBuilder whenArg() {
			return new WhenArgBuilder(this);
		}

		public CommandLineToStringBuilder add(ArgsFunction argsFunction) {
			if(argsFunction != null) {
				argsFunctions.add(argsFunction);
			}
			return this;
		}

		public CommandLineToString build() {
			return new CommandLineToString(this);
		}
	}

	// ------------------------------------------------------------

	public static class WhenArgBuilder {

		private final CommandLineToStringBuilder commandLineToStringBuilder;

		private WhenArgBuilder(CommandLineToStringBuilder commandLineToStringBuilder) {
			this.commandLineToStringBuilder = commandLineToStringBuilder;
		}

		public WhenArgEqualsBuilder equalsTo(String search) {
			assertNotBlank(search, "search");
			return verify(search::equals);
		}

		public WhenArgEqualsBuilder equalsToIgnoreCase(String search) {
			assertNotBlank(search, "search");
			return verify(search::equalsIgnoreCase);
		}

		public WhenArgEqualsBuilder verify(Predicate<String> predicate) {
			Objects.requireNonNull(predicate);
			return new WhenArgEqualsBuilder(commandLineToStringBuilder, predicate);
		}
	}

	// ------------------------------------------------------------

	public static class WhenArgEqualsBuilder {

		private final CommandLineToStringBuilder commandLineToStringBuilder;

		private final Predicate<String> predicate;

		private WhenArgEqualsBuilder(CommandLineToStringBuilder commandLineToStringBuilder, Predicate<String> predicate) {
			this.commandLineToStringBuilder = commandLineToStringBuilder;
			this.predicate = predicate;
		}

		public CommandLineToStringBuilder hide() {
			return replaceBy(HIDE);
		}

		public CommandLineToStringBuilder replaceBy(String replace) {
			assertNotBlank(replace, "replace");
			commandLineToStringBuilder.add(list -> {
				for(int i = 0; i < list.size(); ++i) {
					if(predicate.test(list.get(i))) {
						list.set(i, replace);
					}
				}
				return list;
			});
			return commandLineToStringBuilder;
		}

		public CommandLineToStringBuilder hideNext() {
			return replaceNextBy(HIDE);
		}

		public CommandLineToStringBuilder replaceNextBy(String replace) {
			assertNotBlank(replace, "replace");
			commandLineToStringBuilder.add(list -> {
				for(int i = 0; i < list.size() - 1; ++i) {
					if(predicate.test(list.get(i))) {
						list.set(i + 1, replace);
					}
				}
				return list;
			});
			return commandLineToStringBuilder;
		}
	}
	// ------------------------------------------------------------

	private final Collection<String> args;

	private final List<ArgsFunction> argsFunctions;

	private CommandLineToString(CommandLineToStringBuilder builder) {
		this.args = builder.args;
		this.argsFunctions = Collections.unmodifiableList(new ArrayList<>(builder.argsFunctions));
	}

	public static CommandLineToStringBuilder with(CommandLine commandLine) {
		return with(commandLine.toStrings());
	}

	public static CommandLineToStringBuilder with(String[] args) {
		return with(Arrays.asList(args));
	}

	public static CommandLineToStringBuilder with(Collection<String> args) {
		return new CommandLineToStringBuilder(args);
	}

	@Override
	public String toString() {
		List<String> copy = new ArrayList<>(args);
		for(ArgsFunction argsFunction : argsFunctions) {
			copy = argsFunction.apply(copy);
		}
		return copy.stream()
				.map(s -> {
					if(s.contains(" ") || s.contains("'") || s.contains("=")) {
						return '"' + s + '"';
					}
					return s;
				})
				.collect(Collectors.joining(" "))
				.replace("\n", "\\n")
				.replace("\r", "\\r");
	}

	// ***********************************************************

	private static void assertNotBlank(String value, String name) {
		if(StringUtils.isBlank(value)) {
			throw new IllegalArgumentException("'" + name + "' must be not blank");
		}
	}

}
