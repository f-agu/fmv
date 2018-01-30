package org.fagu.fmv.cli;

/*
 * #%L
 * fmv-cli
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import org.fagu.fmv.core.exec.Executable;


/**
 * @author f.agu
 */
public class BasePrompt implements Prompt {

	/**
	 * @see org.fagu.fmv.cli.Prompt#get(org.fagu.fmv.cli.Environnement)
	 */
	@Override
	public String get(Environnement environnement) {
		Executable currentExecutable = environnement.getCurrentExecutable();
		StringBuilder prompt = new StringBuilder(50);
		if(currentExecutable != null) {
			prompt.append(currentExecutable.getCode()).append('[').append(currentExecutable.getId()).append(']');
		}
		prompt.append("# ");
		return prompt.toString();
	}

}
