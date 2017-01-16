package org.fagu.fmv.soft.exec;

/*
 * #%L
 * fmv-utils
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


import java.io.File;
import java.util.Collection;

import org.apache.commons.exec.CommandLine;


/**
 * @author f.agu
 */
public class FMVCommandLine extends CommandLine {

	/**
	 * @param executable
	 * @param args
	 * @return
	 */
	public static FMVCommandLine create(String executable, Collection<String> args) {
		return new FMVCommandLine(executable).addArgs(args);
	}

	/**
	 * @param executable
	 * @param args
	 * @return
	 */
	public static FMVCommandLine create(String executable, String... args) {
		return new FMVCommandLine(executable).addArgs(args);
	}

	/**
	 * @param executable
	 * @param args
	 * @return
	 */
	public static FMVCommandLine create(File executable, Collection<String> args) {
		return new FMVCommandLine(executable).addArgs(args);
	}

	/**
	 * @param executable
	 * @param args
	 * @return
	 */
	public static FMVCommandLine create(File executable, String... args) {
		return new FMVCommandLine(executable).addArgs(args);
	}

	/**
	 * @param executable
	 */
	public FMVCommandLine(String executable) {
		super(executable);
	}

	/**
	 * @param executable
	 */
	public FMVCommandLine(File executable) {
		super(executable);
	}

	/**
	 * @param other
	 */
	public FMVCommandLine(CommandLine other) {
		super(other);
	}

	/**
	 * @param args
	 * @return
	 */
	public FMVCommandLine addArgs(Collection<String> args) {
		for(String arg : args) {
			addArgument(arg);
		}
		return this;
	}

	/**
	 * @param args
	 * @return
	 */
	public FMVCommandLine addArgs(String... args) {
		for(String arg : args) {
			addArgument(arg);
		}
		return this;
	}

	/**
	 * @see org.apache.commons.exec.CommandLine#addArgument(java.lang.String)
	 */
	@Override
	public CommandLine addArgument(String argument) {
		return super.addArgument(argument, false);
	}

	/**
	 * @see org.apache.commons.exec.CommandLine#addArguments(java.lang.String[])
	 */
	@Override
	public CommandLine addArguments(String[] addArguments) {
		return super.addArguments(addArguments, false);
	}

}
