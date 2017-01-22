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

import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.ffmpeg.utils.Time;

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
	public Long id() {
		return getLong("id");
	}

	/**
	 * @return
	 */
	public Fraction timeBase() {
		return getFraction("time_base");
	}

	/**
	 * @return
	 */
	public Long start() {
		return getLong("start");
	}

	/**
	 * @return
	 */
	public Long end() {
		return getLong("end");
	}

	/**
	 * @return
	 */
	public Time endTime() {
		return getTime("end_time");
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		String title = title();
		if(title != null) {
			buf.append(title).append('=');
		}
		Time startTime = startTime();
		Time endTime = endTime();
		if(startTime != null && endTime != null) {
			buf.append(startTime).append('>').append(endTime);
		} else {
			Long start = start();
			Long end = end();
			if(start != null && end != null) {
				buf.append(start).append('>').append(end);
			}
		}
		return buf.toString();
	}
}
