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
