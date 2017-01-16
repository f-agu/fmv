package org.fagu.fmv.utils;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.utils.collection.MapMap;
import org.fagu.fmv.utils.collection.MultiValueMaps;


/**
 * @author f.agu
 */
public class IniFile {

	private final MapMap<String, String, String> mapMap;

	/**
	 * @param mapMap
	 */
	protected IniFile(MapMap<String, String, String> mapMap) {
		this.mapMap = Objects.requireNonNull(mapMap);
	}

	// ********************

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static IniFile load(File file) throws IOException {
		try (Reader reader = new FileReader(file)) {
			return parse(reader);
		}
	}

	/**
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static IniFile parse(InputStream inputStream) throws IOException {
		return parse(new InputStreamReader(inputStream));
	}

	/**
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public static IniFile parse(Reader reader) throws IOException {
		final Pattern SECTION_PATTERN = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");

		MapMap<String, String, String> mapMap = MultiValueMaps.hashMapHashMap();

		String line = null;
		String section = null;

		BufferedReader br = new BufferedReader(reader);
		while((line = br.readLine()) != null) {
			line = line.trim();
			if(line.isEmpty() || line.startsWith("#")) { // empty or comments
				continue;
			}
			Matcher matcher = SECTION_PATTERN.matcher(line);
			if(matcher.matches()) {
				section = matcher.group(1).trim();
			} else if(section != null) {
				String key;
				String value = null;
				int equalsPos = line.indexOf('=');
				if(equalsPos > 0) {
					key = line.substring(0, equalsPos - 1).trim();
					value = line.substring(equalsPos + 1);
				} else {
					key = line;
				}
				mapMap.add(section, key, value);
			}
		}
		return new IniFile(mapMap);
	}

	// ********************

	/**
	 * @param section
	 * @return
	 */
	public Set<String> getKeys(String section) {
		Map<String, String> map = mapMap.get(section);
		return map != null ? map.keySet() : Collections.emptySet();
	}

	/**
	 * @param section
	 * @return
	 */
	public Map<String, String> getKeyValueMap(String section) {
		return mapMap.get(section);
	}

	/**
	 * @param section
	 * @param key
	 * @return
	 */
	public boolean contains(String section, String key) {
		return mapMap.containsKeys(section, key);
	}

	/**
	 * @param section
	 * @param key
	 * @return
	 */
	public String getString(String section, String key) {
		return getString(section, key, null);
	}

	/**
	 * @param section
	 * @param key
	 * @param defaultvalue
	 * @return
	 */
	public String getString(String section, String key, String defaultvalue) {
		String string = mapMap.get(section, key);
		return string != null ? string : defaultvalue;
	}

}
