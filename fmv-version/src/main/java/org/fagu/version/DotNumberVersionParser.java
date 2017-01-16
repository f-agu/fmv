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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


/**
 * @author f.agu
 */
public class DotNumberVersionParser implements VersionParser {

	public static final String REGEX = "([0-9]+)(\\.[0-9]+)*";

	private static final Pattern PATTERN = Pattern.compile(REGEX);

	public static final DotNumberVersionParser INSTANCE = new DotNumberVersionParser();

	/**
	 *
	 */
	public DotNumberVersionParser() {}

	/**
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(String str) {
		if(str == null) {
			return false;
		}
		return PATTERN.matcher(str).matches();
	}

	/**
	 * @see org.fagu.version.VersionParser#parse(java.lang.String)
	 */
	@Override
	public Version parse(String str) throws VersionParseException {
		if( ! test(str)) {
			throw new VersionParseException(str);
		}
		final int maxSize = VersionUnit.upper().getPosition();
		StringTokenizer st = new StringTokenizer(str, ".");
		List<Integer> list = new ArrayList<>();
		while(st.hasMoreTokens() && list.size() < maxSize) {
			list.add(Integer.parseInt(st.nextToken()));
		}
		return new Version(list);
	}
}
