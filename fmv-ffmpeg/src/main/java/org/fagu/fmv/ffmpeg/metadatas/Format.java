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
import java.util.OptionalInt;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;


/**
 * @author f.agu
 */
public class Format extends InfoBase {

	/**
	 * @param movieMetadatas
	 * @param map
	 */
	public Format(MovieMetadatas movieMetadatas, NavigableMap<String, Object> map) {
		super(movieMetadatas, map);
	}

	/**
	 * @param jsonObject
	 * @param movieMetadatas
	 * @return
	 */
	public static Format create(JSONObject jsonObject, MovieMetadatas movieMetadatas) {
		return new Format(movieMetadatas, MovieMetadatas.createMap(jsonObject));
	}

	/**
	 * @return
	 */
	public String fileName() {
		return getString("filename");
	}

	/**
	 * @return
	 */
	public String formatLongName() {
		return getString("format_long_name");
	}

	/**
	 * @return
	 */
	public String formatName() {
		return getString("format_name");
	}

	/**
	 * @return
	 */
	public OptionalInt size() {
		return getInt("size");
	}

	/**
	 * @return
	 */
	public String compatibleBrands() {
		return StringUtils.stripToEmpty((String)tag("compatible_brands"));
	}

	/**
	 * @return
	 */
	public String majorBrand() {
		return StringUtils.stripToEmpty((String)tag("major_brand"));
	}

	/**
	 * @return
	 */
	public String title() {
		return (String)tag("title");
	}

	/**
	 * @return
	 */
	public String album() {
		return (String)tag("album");
	}

	/**
	 * @return
	 */
	public String year() {
		return (String)tag("date");
	}

	/**
	 * @return
	 */
	public String comment() {
		return (String)tag("comment");
	}

	/**
	 * @return
	 */
	public String artist() {
		return (String)tag("artist");
	}

	/**
	 * @return
	 */
	public String genre() {
		return (String)tag("genre");
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append("Format[").append(formatName()).append(']');
		return buf.toString();
	}

}
