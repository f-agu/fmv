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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author f.agu
 * @created 21 nov. 2017 12:07:21
 */
public class EnvironmentExecuteDelegate implements ExecuteDelegate {

	private final Map<String, String> envs = new HashMap<>(System.getenv());

	public EnvironmentExecuteDelegate() {}

	public EnvironmentExecuteDelegate(Map<String, String> envs) {
		this.envs.putAll(envs);
	}

	public static EnvironmentExecuteDelegate systemEnvs() {
		return new EnvironmentExecuteDelegate();
	}

	@Override
	public int execute(FMVExecutor fmvExecutor, CommandLine commandLine) throws IOException {
		return fmvExecutor.execute(commandLine, envs);
	}

}
