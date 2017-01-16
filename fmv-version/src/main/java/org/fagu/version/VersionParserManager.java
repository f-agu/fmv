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


/**
 * @author f.agu
 */
public class VersionParserManager {

	private static final List<VersionParser> PARSERS = new ArrayList<>(2);

	static {
		addParser(new DotNumberVersionParser());
		addParser(new RemoveOtherVersionParser());
	}

	/**
	 *
	 */
	private VersionParserManager() {}

	// ********************************************************

	/**
	 * @param str
	 */
	public static boolean isParsable(String str) {
		for(VersionParser versionParser : PARSERS) {
			if(versionParser.test(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param str
	 * @return
	 * @throws VersionParseException
	 */
	public static Version parse(String str) throws VersionParseException {
		for(VersionParser versionParser : PARSERS) {
			if(versionParser.test(str)) {
				return versionParser.parse(str);
			}
		}
		throw new VersionParseException(str);
	}

	/**
	 * @param versionParser
	 */
	public static void addParser(VersionParser versionParser) {
		if(versionParser != null) {
			PARSERS.add(versionParser);
		}
	}
}
