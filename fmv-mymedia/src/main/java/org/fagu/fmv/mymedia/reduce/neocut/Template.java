package org.fagu.fmv.mymedia.reduce.neocut;

/*-
 * #%L
 * fmv-mymedia
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author Utilisateur
 * @created 4 avr. 2018 14:47:54
 */
public class Template {

	private final String name;

	private final Map<Time, File> modelMap;

	private final Time offsetStart;

	private final Duration endDuration;

	private final Logo logo;

	private Template(String name, Map<Time, File> modelMap, Time offsetStart, Duration endDuration, Logo logo) {
		this.name = Objects.requireNonNull(name);
		this.modelMap = Collections.unmodifiableMap(modelMap);
		this.offsetStart = offsetStart;
		this.endDuration = endDuration;
		this.logo = logo;
	}

	public static Template load(File propertiesFile) throws IOException {
		Properties properties = new Properties();
		try (InputStream inputStream = new FileInputStream(propertiesFile)) {
			properties.load(inputStream);
		}

		Map<Time, File> modelMap = new HashMap<>();
		for(Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			if(key.startsWith("model.")) {
				Time time = Time.parse(key.substring(6));
				File file = findModel(propertiesFile, value);
				modelMap.put(time, file);
			}
		}

		String name = properties.getProperty("name");
		Time offsetStart = parseTime(properties.getProperty("offset-time"));
		Duration endDuration = parseDuration(properties.getProperty("end-duration"));
		Logo logo = loadLogo(properties);
		return new Template(name, modelMap, offsetStart, endDuration, logo);
	}

	public String getName() {
		return name;
	}

	public Map<Time, File> getModelMap() {
		return modelMap;
	}

	public Time getOffsetStart() {
		return offsetStart;
	}

	public Duration getEndDuration() {
		return endDuration;
	}

	public Logo getLogo() {
		return logo;
	}

	@Override
	public String toString() {
		return name;
	}

	// ******************************************************

	private static File findModel(File propertiesFile, String fileName) throws IOException {
		File file = new File(fileName);
		if(file.exists()) {
			return file;
		}
		file = new File(propertiesFile.getParentFile(), fileName);
		if(file.exists()) {
			return file;
		}
		throw new FileNotFoundException(fileName + " declared in " + propertiesFile);
	}

	private static Time parseTime(String s) {
		return s != null ? Time.parse(s) : null;
	}

	private static Duration parseDuration(String s) {
		return s != null ? Duration.parse(s) : null;
	}

	private static Logo loadLogo(Properties properties) {
		if(Boolean.valueOf(properties.getProperty("logo.autoDetect", "false"))) {
			return Logo.autoDetect();
		}
		String strX = properties.getProperty("logo.x");
		String strY = properties.getProperty("logo.y");
		String strW = properties.getProperty("logo.w");
		String strH = properties.getProperty("logo.h");
		if(strX == null && strY == null && strW == null && strH == null) {
			return null;
		}
		int x = Integer.parseInt(strX);
		int y = Integer.parseInt(strY);
		int w = Integer.parseInt(strW);
		int h = Integer.parseInt(strH);
		return Logo.defined(x, y, w, h);
	}

}
