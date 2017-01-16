package org.fagu.fmv.utils;

/*
 * #%L
 * fmv-utils
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

/**
 * @author fagu
 */
public class PlaceHolderException extends RuntimeException {

	private static final long serialVersionUID = 1162784810617412582L;

	/**
	 *
	 */
	public PlaceHolderException() {}

	/**
	 * @param message
	 */
	public PlaceHolderException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public PlaceHolderException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PlaceHolderException(String message, Throwable cause) {
		super(message, cause);
	}

}
