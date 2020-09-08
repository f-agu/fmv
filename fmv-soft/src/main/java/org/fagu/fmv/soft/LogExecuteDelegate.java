package org.fagu.fmv.soft;

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

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author Oodrive
 * @author f.agu
 * @created 30 juil. 2020 14:46:56
 */
public class LogExecuteDelegate implements ExecuteDelegate {

	private final ExecuteDelegate delegated;

	private final Consumer<String> logConsumer;

	public LogExecuteDelegate(Consumer<String> logConsumer) {
		this(BasicExecuteDelegate.INSTANCE, logConsumer);
	}

	public LogExecuteDelegate(ExecuteDelegate delegated, Consumer<String> logConsumer) {
		this.delegated = Objects.requireNonNull(delegated);
		this.logConsumer = logConsumer;
	}

	@Override
	public int execute(FMVExecutor fmvExecutor, CommandLine commandLine) throws IOException {
		Integer exitValue = null;
		try {
			exitValue = delegated.execute(fmvExecutor, commandLine);
		} finally {
			if(logConsumer != null) {
				logConsumer.accept(CommandLineUtils.toLine(commandLine) + " ; exitValue: " + (exitValue != null ? exitValue : "failed"));
			}
		}
		return Objects.requireNonNull(exitValue);
	}

}
