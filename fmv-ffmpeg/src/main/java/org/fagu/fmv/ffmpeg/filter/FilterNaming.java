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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.filter.Label.State;


/**
 * @author f.agu
 */
public class FilterNaming {

	private static final char[] DIGITS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p'};

	private int counter = 0;

	private final Set<String> names;

	private final Map<Label, String> labelMap;

	/**
	 * 
	 */
	public FilterNaming() {
		names = new LinkedHashSet<>();
		labelMap = new HashMap<>();
	}

	/**
	 * @param label
	 * @return
	 */
	public String generate(Label label) {
		if(label.getState() == State.INPUT) {
			return label.getName();
		}
		String name = labelMap.get(label);
		if(name != null) {
			return name;
		}
		String prefix = label.getName();
		name = nextUnused(prefix != null ? prefix + '_' : StringUtils.EMPTY);
		names.add(name);
		labelMap.put(label, name);
		return name;
	}

	/**
	 * @param label
	 * @return
	 */
	public String generateBrackets(Label label) {
		String gen = generate(label);
		if(label.getState() == State.INPUT) {
			return gen;
		}
		return '[' + gen + ']';
	}

	/**
	 * 
	 */
	public void reset() {
		counter = 0;
		names.clear();
	}

	// **********************************************************

	/**
	 * @return
	 */
	private String nextUnused(String prefix) {
		String s = null;
		while(names.contains(s = prefix + toUnsignedString(counter++, 4)));
		return s;
	}

	/**
	 * @param i
	 * @param shift
	 * @return
	 */
	private static String toUnsignedString(int i, int shift) {
		char[] buf = new char[32];
		int charPos = 32;
		int radix = 1 << shift;
		int mask = radix - 1;
		do {
			buf[--charPos] = DIGITS[i & mask];
			i >>>= shift;
		} while(i != 0);

		return new String(buf, charPos, (32 - charPos));
	}

}
