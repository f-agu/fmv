package org.fagu.fmv.ffmpeg.metadatas;

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

import java.util.NavigableMap;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.fagu.fmv.utils.geo.Coordinates;

import net.sf.json.JSONObject;


/**
 * @author f.agu
 */
public class Format extends InfoBase {

	public Format(MovieMetadatas movieMetadatas, NavigableMap<String, Object> map) {
		super(movieMetadatas, map);
	}

	public static Format create(JSONObject jsonObject, MovieMetadatas movieMetadatas) {
		return new Format(movieMetadatas, MovieMetadatas.createMap(jsonObject));
	}

	@Override
	public String getName() {
		return "format";
	}

	public Optional<String> fileName() {
		return getString("filename");
	}

	public Optional<String> formatLongName() {
		return getString("format_long_name");
	}

	public Optional<String> formatName() {
		return getString("format_name");
	}

	public OptionalInt size() {
		return getInt("size");
	}

	public Optional<String> compatibleBrands() {
		return tagString("compatible_brands").map(StringUtils::stripToEmpty);
	}

	public Optional<String> majorBrand() {
		return tagString("major_brand").map(StringUtils::stripToEmpty);
	}

	public Optional<String> album() {
		return tagString("album");
	}

	public Optional<String> year() {
		return tagString("date");
	}

	public Optional<String> comment() {
		return tagString("comment");
	}

	public Optional<String> artist() {
		return tagString("artist");
	}

	public Optional<String> genre() {
		return tagString("genre");
	}

	public Optional<Coordinates> coordinates() {
		return tagString("location") // +48.8617+002.3547/
				.map(s -> {
					Pattern pattern = Pattern.compile("((?:\\+|-)\\d+(?:\\.\\d+)?)((?:\\+|-)\\d+(?:\\.\\d+)?)/");
					Matcher matcher = pattern.matcher(s);
					if( ! matcher.matches()) {
						return null;
					}
					double latitude = Double.parseDouble(matcher.group(1));
					double longitude = Double.parseDouble(matcher.group(2));
					return new Coordinates(latitude, longitude);
				});
	}

	@Override
	public String toString() {
		return new StringBuilder(100)
				.append("Format[").append(formatName().orElse("?")).append(']')
				.toString();
	}

}
