package org.fagu.fmv.core.project;

/*
 * #%L
 * fmv-core
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

import org.dom4j.Element;


/**
 * @author f.agu
 */
public class LoadException extends Exception {

	private static final long serialVersionUID = 3244594635709561707L;

	/**
	 *
	 */
	public LoadException() {}

	/**
	 * @param message
	 */
	public LoadException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LoadException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LoadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public LoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param element
	 * @param nameMiss
	 * @return
	 */
	public static LoadException missElement(Element element, String nameMiss) {
		return new LoadException("The node \"" + nameMiss + "\" is missing in \"" + element.getPath() + '"');
	}

	/**
	 * @param element
	 * @param nameMiss
	 * @return
	 */
	public static LoadException attribute(Element element, String attributeName) {
		return new LoadException("Unable to get the attribute \"" + attributeName + "\" in \"" + element.getPath() + '"');
	}

}
