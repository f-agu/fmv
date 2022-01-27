package org.fagu.fmv.ffmpeg.utils.srcgen;

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

import java.text.BreakIterator;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class ClassNameUtils {

	private ClassNameUtils() {}

	public static String type(String name) {
		String newName = name.replaceAll("-|_", " ");
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(newName);

		StringBuilder buf = new StringBuilder();
		int start = boundary.first();
		for(int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
			String sub = newName.substring(start, end);
			if(StringUtils.isNotBlank(sub)) {
				buf.append(StringUtils.capitalize(sub));
			}
		}

		newName = buf.toString();
		if(Character.isDigit(newName.charAt(0))) {
			newName = '_' + newName;
		}
		return newName;
	}

	public static String field(String name) {
		String newName = name.replaceAll("-|_", " ");
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(newName);

		StringBuilder buf = new StringBuilder();
		int start = boundary.first();
		for(int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
			String sub = newName.substring(start, end);
			if(StringUtils.isNotBlank(sub)) {
				if(buf.length() == 0) {
					buf.append(sub);
				} else {
					buf.append(StringUtils.capitalize(sub));
				}
			}
		}

		newName = buf.toString();
		if(Character.isDigit(newName.charAt(0))) {
			newName = '_' + newName;
		}
		return newName;
	}

	public static String fieldStatic(String name) {
		String fieldName = name.toUpperCase();
		if(Character.isDigit(fieldName.charAt(0))) {
			fieldName = '_' + fieldName;
		}
		fieldName = fieldName.replace('-', '_');
		fieldName = fieldName.replace('(', '_');
		fieldName = fieldName.replace('.', '_');
		fieldName = fieldName.replaceAll("\\)", "");
		return fieldName;
	}

}
