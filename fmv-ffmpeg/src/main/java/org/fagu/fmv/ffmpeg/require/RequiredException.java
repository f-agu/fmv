package org.fagu.fmv.ffmpeg.require;

/*
 * #%L
 * fmv-ffmpeg
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
public class RequiredException extends RuntimeException {

	private static final long serialVersionUID = - 1098714544221071113L;

	public RequiredException() {}

	public RequiredException(String message) {
		super(message);
	}

	public RequiredException(Throwable cause) {
		super(cause);
	}

	public RequiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
