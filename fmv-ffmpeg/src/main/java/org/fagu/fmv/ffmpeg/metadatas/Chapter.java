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
import java.util.OptionalLong;

import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.utils.time.Time;

import net.sf.json.JSONObject;


/**
 * @author f.agu
 */
public class Chapter extends InfoBase {

	/**
	 * @param movieMetadatas
	 * @param map
	 */
	protected Chapter(MovieMetadatas movieMetadatas, NavigableMap<String, Object> map) {
		super(movieMetadatas, map);
	}

	/**
	 * @param jsonObject
	 * @param movieMetadatas
	 * @return
	 */
	public static Chapter create(JSONObject jsonObject, MovieMetadatas movieMetadatas) {
		NavigableMap<String, Object> map = MovieMetadatas.createMap(jsonObject);
		return new Chapter(movieMetadatas, map);
	}

	/**
	 * @return
	 */
	public OptionalLong id() {
		return getLong("id");
	}

	/**
	 * @return
	 */
	public Optional<Fraction> timeBase() {
		return getFraction("time_base");
	}

	/**
	 * @return
	 */
	public OptionalLong start() {
		return getLong("start");
	}

	/**
	 * @return
	 */
	public OptionalLong end() {
		return getLong("end");
	}

	/**
	 * @return
	 */
	public Optional<Time> endTime() {
		return getTime("end_time");
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		String title = title().orElse(null);
		if(title != null) {
			buf.append(title).append('=');
		}
		Time startTime = startTime().orElse(null);
		Time endTime = endTime().orElse(null);
		if(startTime != null && endTime != null) {
			buf.append(startTime).append('>').append(endTime);
		} else {
			OptionalLong start = start();
			OptionalLong end = end();
			if(start.isPresent() && end.isPresent()) {
				buf.append(start.getAsLong()).append('>').append(end.getAsLong());
			}
		}
		return buf.toString();
	}
}
