package org.fagu.fmv.mymedia.movie;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import java.util.Collection;
import java.util.List;

import org.fagu.fmv.ffmpeg.metadatas.Stream;


/**
 * @author f.agu
 */
public class StreamOrder {

	/**
	 *
	 */
	private StreamOrder() {}

	/**
	 * @param streams
	 * @return
	 */
	public static <T extends Stream> Collection<T> sort(Collection<T> streams) {
		T fr = streams.stream().filter(s -> s.language() != null && s.language().toLowerCase().startsWith("fr")).findFirst().orElse(null);
		T en = streams.stream().filter(s -> s.language() != null
				&& (s.language().toLowerCase().startsWith("ang") || s.language().toLowerCase().startsWith("en"))).findFirst().orElse(null);
		List<T> newStreams = new ArrayList<>();
		if(fr != null) {
			newStreams.add(fr);
		}
		if(en != null) {
			newStreams.add(en);
		}
		streams.stream().filter(s -> s != fr && s != en).forEach(newStreams::add);
		return newStreams;
	}

}
