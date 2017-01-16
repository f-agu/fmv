package org.fagu.version;

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

/**
 * @author f.agu
 */
public class VersionParseException extends RuntimeException {

	private static final long serialVersionUID = 997348986526448883L;

	/**
	 * @param str a string that explains what the exception condition is
	 */
	public VersionParseException(String str, Throwable cause) {
		super(str, cause);
	}

	/**
	 * Default constructor.
	 */
	public VersionParseException(Throwable cause) {
		super(cause);
	}

	/**
	 * Default constructor.
	 */
	public VersionParseException(String str) {
		super(str);
	}
}
