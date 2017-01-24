package org.fagu.fmv.soft.exec.exception;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import java.util.Objects;


/**
 * @author fagu
 */
public class SimpleExceptionKnownAnalyzer implements ExceptionKnownAnalyzer {

	private final String title;

	private final String strToFind;

	/**
	 * @param title
	 * @param strToFind
	 */
	public SimpleExceptionKnownAnalyzer(String title, String strToFind) {
		this.title = Objects.requireNonNull(title);
		this.strToFind = Objects.requireNonNull(strToFind);
	}

	/**
	 * @see org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer#anaylze(org.fagu.fmv.soft.exec.exception.NestedException)
	 */
	@Override
	public ExceptionKnown anaylze(NestedException nestedException) {
		if(nestedException.contains(strToFind)) {
			return new ExceptionKnown(title);
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return
	 */
	public String getStrToFind() {
		return strToFind;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return title;
	}

}
