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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.fagu.fmv.utils.collection.LimitedFirstQueue;
import org.fagu.fmv.utils.collection.LimitedLastQueue;


/**
 * To limit the buffer size, you can use {@link LimitedFirstQueue} or {@link LimitedLastQueue}. Example:
 * 
 * <pre>
 * 
 * ReadLine readLine = new BufferedReadLine(new LimitedLastQueue(10));
 * </pre>
 * 
 * @author f.agu
 */
public class BufferedReadLine implements ReadLine {

	private final Collection<String> collection;

	public BufferedReadLine() {
		this(new ArrayList<>());
	}

	public BufferedReadLine(Collection<String> collection) {
		this.collection = Objects.requireNonNull(collection);
	}

	@Override
	public void read(String line) {
		collection.add(line);
	}

	public Collection<String> getLines() {
		return collection;
	}

	@Override
	public String toString() {
		return "Buffered";
	}

}
