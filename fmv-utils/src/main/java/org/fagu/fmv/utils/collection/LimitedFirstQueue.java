package org.fagu.fmv.utils.collection;

/*-
 * #%L
 * fmv-utils
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

import java.util.LinkedList;


/**
 * @author fagu
 *
 * @param <E>
 */
public class LimitedFirstQueue<E> extends LinkedList<E> {

	private static final long serialVersionUID = - 4919982990481713127L;

	private final int limit;

	public LimitedFirstQueue(int limit) {
		if(limit <= 0) {
			throw new IllegalArgumentException("limit must at least 1: " + limit);
		}
		this.limit = limit;
	}

	@Override
	public boolean add(E o) {
		if(size() >= limit) {
			return false;
		}
		return super.add(o);
	}
}
