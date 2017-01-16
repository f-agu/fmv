package org.fagu.fmv.core.exec;

/*
 * #%L
 * fmv-core
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author f.agu
 */
public class ExecutableOption {

	private static final Map<String, ExecutableOption> NAME_MAP = new HashMap<>();

	public static final ExecutableOption PROPAGATION_MAKE_BACKGROUND = new ExecutableOption("PROPAGATION_MAKE_BACKGROUND");

	public static final ExecutableOption STOP_PROPAGATION_MAKE_BACKGROUND = new ExecutableOption("STOP_PROPAGATION_MAKE_BACKGROUND");

	private final String name;

	/**
	 * @param name
	 */
	public ExecutableOption(String name) {
		this.name = Objects.requireNonNull(name);
		NAME_MAP.put(name.toUpperCase(), this);
	}

	/**
	 * @return
	 */
	public String name() {
		return name;
	}

	// **********************

	/**
	 * @param name
	 * @return
	 */
	public static ExecutableOption valueOf(String name) {
		ExecutableOption executableOption = NAME_MAP.get(name.toUpperCase());
		if(executableOption == null) {
			throw new IllegalArgumentException("Undefined name: " + name);
		}
		return executableOption;
	}

	/**
	 * @return
	 */
	public static Collection<ExecutableOption> values() {
		return Collections.unmodifiableCollection(NAME_MAP.values());
	}

}
