package org.fagu.fmv.ffmpeg.operation;

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

import java.util.HashSet;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.FilterComplexBase;


/**
 * @author f.agu
 */
public enum Type {

	AUDIO, VIDEO, SUBTITLE, DATA, ATTACHEMENTS, UNKNOWN;

	/**
	 * @return
	 */
	public char code() {
		return Character.toLowerCase(name().charAt(0));
	}

	/**
	 * @return
	 */
	public static Set<Type> valuesSet() {
		Type[] values = Type.values();
		Set<Type> set = new HashSet<Type>(values.length);
		for(Type type : values) {
			set.add(type);
		}
		return set;
	}

	/**
	 * @param filterComplexBase
	 * @return
	 */
	public static Set<Type> valuesSet(FilterComplexBase filterComplexBase) {
		Type[] values = Type.values();
		Set<Type> set = new HashSet<Type>(values.length);
		for(Type type : values) {
			if(filterComplexBase.containsInput(type)) {
				set.add(type);
			}
		}
		return set;
	}

	/**
	 * @param parameter
	 * @param type
	 * @return
	 */
	public static String concat(String parameter, Type type) {
		if(type == null) {
			return parameter;
		}
		return new StringBuilder(parameter.length() + 2).append(parameter).append(':').append(type.code()).toString();
	}

}
