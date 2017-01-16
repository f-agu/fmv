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

import java.io.IOException;


/**
 * @author f.agu
 */
public class ExecutableFactory extends IdentifiableFactory<Executable> {

	private static final ExecutableFactory EXECUTABLE_FACTORY = new ExecutableFactory();

	/**
	 *
	 */
	public ExecutableFactory() {
		super(Executable.class);
		try {
			register(Executable.class.getPackage().getName());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return
	 */
	public static ExecutableFactory getSingleton() {
		return EXECUTABLE_FACTORY;
	}
}
