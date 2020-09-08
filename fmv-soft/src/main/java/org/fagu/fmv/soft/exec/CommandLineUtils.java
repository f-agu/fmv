package org.fagu.fmv.soft.exec;

/*-
 * #%L
 * fmv-soft
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
