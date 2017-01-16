package org.fagu.fmv.ffmpeg.filter;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
class Inputs {

	// ---------------------------------------------------------

	/**
	 * @author f.agu
	 */
	private class In {

		private final int inputIndex;

		private final Type type;

		/**
		 * @param inputIndex
		 * @param type
		 */
		private In(int inputIndex, Type type) {
			this.inputIndex = inputIndex;
			this.type = type;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return new StringBuilder().append('[').append(inputIndex).append(':').append(type.code()).append(']').toString();
		}

	}

	// ---------------------------------------------------------

	private final List<In> ins;

	private final Set<Type> types = new HashSet<>(Type.values().length);

	private final Set<Integer> index = new HashSet<>();

	/**
	 * 
	 */
	Inputs() {
		ins = new ArrayList<>(4);
	}

	/**
	 * @param indexStream
	 * @param type
	 */
	void add(int indexStream, Type type) {
		ins.add(new In(indexStream, type));
		types.add(type);
		index.add(indexStream);
	}

	/**
	 * @return
	 */
	boolean isEmpty() {
		return ins.isEmpty();
	}

	/**
	 * @param type
	 * @return
	 */
	boolean contains(Type type) {
		return types.contains(type);
	}

	/**
	 * @return
	 */
	int countInput() {
		return index.size();
	}

	/**
	 * @return
	 */
	int countTypes() {
		return types.size();
	}

	/**
	 * @param buf
	 * @param inTypes
	 */
	void appendHeader(StringBuilder buf, Type... inTypes) {
		Type[] types = inTypes;
		if(types == null || types.length == 0) {
			types = this.types.toArray(new Type[this.types.size()]);
		}
		for(In in : ins) {
			if(ArrayUtils.contains(types, in.type)) {
				buf.append(in.toString());
			}
		}
	}

}
