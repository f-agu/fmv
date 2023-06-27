package org.fagu.fmv.utils;

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

import java.util.Iterator;


/**
 * @author f.agu
 */
public class StringJoin {

	private StringJoin() {}

	public static void append(StringBuilder buf, Iterable<?> iterable, char separator) {
		if(iterable == null) {
			return;
		}
		append(buf, iterable.iterator(), separator);
	}

	public static void append(StringBuilder buf, Iterable<?> iterable, String separator) {
		if(iterable == null) {
			return;
		}
		append(buf, iterable.iterator(), separator);
	}

	public static void append(StringBuilder buf, Iterable<?> iterable, String separator, String wrapBefore, String wrapAfter) {
		if(iterable == null) {
			return;
		}
		append(buf, iterable.iterator(), separator, wrapBefore, wrapAfter);
	}

	public static void append(StringBuilder buf, Iterator<?> iterator, char separator) {
		if(iterator == null) {
			return;
		}
		for(;;) {
			Object o = iterator.next();
			buf.append(String.valueOf(o));
			if( ! iterator.hasNext()) {
				return;
			}
			buf.append(separator);
		}
	}

	public static void append(StringBuilder buf, Iterator<?> iterator, String separator) {
		if(iterator == null) {
			return;
		}
		for(;;) {
			Object o = iterator.next();
			buf.append(String.valueOf(o));
			if( ! iterator.hasNext()) {
				return;
			}
			buf.append(separator);
		}
	}

	public static void append(StringBuilder buf, Iterator<?> iterator, String separator, String wrapBefore, String wrapAfter) {
		if(iterator == null) {
			return;
		}
		for(;;) {
			Object o = iterator.next();
			buf.append(wrapBefore).append(String.valueOf(o)).append(wrapAfter);
			if( ! iterator.hasNext()) {
				return;
			}
			buf.append(separator);
		}
	}

}
