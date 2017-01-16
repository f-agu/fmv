package org.fagu.fmv.soft.exec;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;


/**
 * @author f.agu
 */
public class MultiReadLine implements ReadLine {

	private final Collection<ReadLine> readLines;

	/**
	 * @param readLines
	 */
	public MultiReadLine(ReadLine... readLines) {
		this.readLines = Arrays.asList(readLines);
	}

	/**
	 * @param readLines
	 */
	public MultiReadLine(Collection<ReadLine> readLines) {
		this.readLines = Objects.requireNonNull(readLines);
	}

	/**
	 * @see org.fagu.fmv.utils.exec.ReadLine#read(java.lang.String)
	 */
	@Override
	public void read(String line) {
		for(ReadLine readLine : readLines) {
			readLine.read(line);
		}
	}
}
