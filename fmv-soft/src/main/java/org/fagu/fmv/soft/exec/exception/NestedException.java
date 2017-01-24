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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fagu.fmv.soft.FMVExecuteException;


/**
 * @author f.agu
 */
public class NestedException {

	private final Exception exception;

	private List<String> msgLines;

	/**
	 * @param exception
	 */
	public NestedException(Exception exception) {
		this.exception = exception;
	}

	/**
	 * @return
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @return
	 */
	public boolean isFMVExecutorException() {
		return exception instanceof FMVExecuteException;
	}

	/**
	 * @return
	 */
	public List<String> messageToLines() {
		if(msgLines == null) {
			List<String> list = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new StringReader(exception.getMessage()))) {
				String line = null;
				while((line = reader.readLine()) != null) {
					list.add(line);
				}
			} catch(IOException e) {
				// never append
			}
			msgLines = Collections.unmodifiableList(list);
		}
		return msgLines;
	}

	/**
	 * @param strToFind
	 * @return
	 */
	public boolean contains(String strToFind) {
		return exception.getMessage().contains(strToFind);
	}
}
