package org.fagu.fmv.cli;

/*
 * #%L
 * fmv-cli
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

import java.util.Objects;
import java.util.Properties;


/**
 * @author f.agu
 */
public class FMVCLIConfig {

	private final Properties properties;

	/**
	 *
	 */
	public FMVCLIConfig() {
		this(new Properties());
	}

	/**
	 * @param properties
	 */
	public FMVCLIConfig(Properties properties) {
		this.properties = Objects.requireNonNull(properties);
	}

	/**
	 * @param name
	 * @return
	 */
	public String getProperty(String name) {
		return properties.getProperty(name);
	}
}
