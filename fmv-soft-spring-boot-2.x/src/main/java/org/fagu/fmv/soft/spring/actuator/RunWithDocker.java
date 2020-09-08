package org.fagu.fmv.soft.spring.actuator;

/*-
 * #%L
 * fmv-soft-spring-boot-2.x
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

import java.io.File;


/**
 * @author f.agu
 * @created 7 août 2020 11:37:51
 */
public class RunWithDocker {

	private static final String DOCKER_ENV_FILE = "/.dockerenv";

	private RunWithDocker() {}

	public static boolean isInDocker() {
		return new File(DOCKER_ENV_FILE).exists();
	}

}
