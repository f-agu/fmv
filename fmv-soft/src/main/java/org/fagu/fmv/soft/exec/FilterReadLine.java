package org.fagu.fmv.soft.exec;

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

import java.util.Objects;
import java.util.function.Predicate;


/**
 * @author f.agu
 * @created 8 sept. 2020 13:54:07
 */
public class FilterReadLine implements ReadLine {

	private final ReadLine readLine;

	private final Predicate<String> filterLine;

	public FilterReadLine(ReadLine readLine, Predicate<String> filterLine) {
		this.readLine = Objects.requireNonNull(readLine);
		this.filterLine = Objects.requireNonNull(filterLine);
	}

	@Override
	public void read(String line) {
		if(filterLine.test(line)) {
			readLine.read(line);
		}
	}

}
