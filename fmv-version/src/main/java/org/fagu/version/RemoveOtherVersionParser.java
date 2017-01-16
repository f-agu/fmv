package org.fagu.version;

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

/**
 * @author f.agu
 */
public class RemoveOtherVersionParser implements VersionParser {

	/**
	 * 
	 */
	public RemoveOtherVersionParser() {}

	/**
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(String str) {
		if(str == null) {
			return false;
		}
		String newValue = getNewValue(str);
		if(newValue == null || "".equals(newValue)) {
			return false;
		}
		return DotNumberVersionParser.INSTANCE.test(newValue);
	}

	/**
	 * @see org.fagu.version.VersionParser#parse(java.lang.String)
	 */
	@Override
	public Version parse(String str) throws VersionParseException {
		if( ! test(str)) {
			throw new VersionParseException(str);
		}
		return new ExtendedVersion(str, DotNumberVersionParser.INSTANCE.parse(getNewValue(str)));
	}

	// ************************************************
	/**
	 * @param value
	 * @return
	 */
	private static String getNewValue(String value) {
		if(value == null || "".equals(value)) {
			return null;
		}
		char firstChar = value.charAt(0);
		if( ! Character.isDigit(firstChar)) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		int len = value.length();
		boolean lastIsDot = true;
		for(int i = 0; i < len; ++i) {
			char c = value.charAt(i);
			if(c <= '9' && c >= '0') {
				buf.append(c);
				lastIsDot = false;
			} else if( ! lastIsDot) {
				buf.append('.');
				lastIsDot = true;
			}
		}
		String newValue = buf.toString();
		if(newValue.endsWith(".")) {
			newValue = newValue.substring(0, newValue.length() - 1);
		}
		return newValue;
	}
}
