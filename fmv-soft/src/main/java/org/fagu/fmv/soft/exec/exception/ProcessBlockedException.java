package org.fagu.fmv.soft.exec.exception;

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


/**
 * @author fagu
 */
public class ProcessBlockedException extends IOException {

	private static final long serialVersionUID = 5480038730803650064L;

	public ProcessBlockedException() {}

	public ProcessBlockedException(String message) {
		super(message);
	}

	public ProcessBlockedException(Throwable cause) {
		super(cause);
	}

	public ProcessBlockedException(String message, Throwable cause) {
		super(message, cause);
	}

}
