package org.fagu.fmv.ffmpeg.operation;

/*
 * #%L
 * fmv-ffmpeg
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

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.soft.exec.ReadLine;
import org.fagu.fmv.utils.collection.MapList;
import org.fagu.fmv.utils.collection.MultiValueMaps;


/**
 * @author f.agu
 */
public class LibLogReadLine implements ReadLine {

	private static final Pattern PATTERN = Pattern.compile("\\[([A-Za-z0-9_]+) @ ([A-Za-z0-9_]+)\\] (.*)");

	private MapList<Predicate<String>, LibLog> libLogMap;

	/**
	 *
	 */
	public LibLogReadLine() {
		libLogMap = MultiValueMaps.hashMapArrayList();
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return libLogMap.isEmpty();
	}

	/**
	 * @param filter
	 * @param libLog
	 */
	public void add(Predicate<String> filter, LibLog libLog) {
		Objects.requireNonNull(filter);
		Objects.requireNonNull(libLog);
		libLogMap.add(filter, libLog);
	}

	/**
	 * @see org.fagu.fmv.utils.exec.ReadLine#read(java.lang.String)
	 */
	@Override
	public void read(String line) {
		if(libLogMap.isEmpty()) {
			return;
		}
		Matcher matcher = PATTERN.matcher(line);
		if(matcher.matches()) {
			String title = matcher.group(1);
			String somethings = matcher.group(2);
			String log = matcher.group(3);

			Set<LibLog> libLogs = new HashSet<>();
			for(Entry<Predicate<String>, List<LibLog>> entrySet : libLogMap.entrySet()) {
				if(entrySet.getKey().test(title)) {
					libLogs.addAll(entrySet.getValue());
				}
			}
			libLogs.stream().forEach(l -> l.log(title, somethings, log));
		}
	}

}
