package org.fagu.fmv.ffmpeg.metadatas;

/*-
 * #%L
 * fmv-ffmpeg
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

import java.util.Map;
import java.util.Optional;

import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class Chapter extends InfoBase {

	protected Chapter(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	@Override
	public String getName() {
		return "chapter";
	}

	public Optional<Long> id() {
		return getLong("id");
	}

	public Optional<Fraction> timeBase() {
		return getFraction("time_base");
	}

	public Optional<Long> start() {
		return getLong("start");
	}

	public Optional<Long> end() {
		return getLong("end");
	}

	public Optional<Time> endTime() {
		return getTime("end_time");
	}

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
			Optional<Long> start = start();
			Optional<Long> end = end();
			if(start.isPresent() && end.isPresent()) {
				buf.append(start.get()).append('>').append(end.get());
			}
		}
		return buf.toString();
	}
}
