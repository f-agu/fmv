package org.fagu.fmv.ffmpeg.soft;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.fagu.version.Version;


/**
 * @author f.agu
 * @created 24 janv. 2017 12:25:47
 */
public class BuildMapping {

	private static final NavigableMap<Version, LocalDate> VERSION_DATE_MAP = versionDateMap();

	private static final NavigableMap<Integer, LocalDate> BUILD_DATE_MAP = buildDateMap();

	/**
	 * 
	 */
	private BuildMapping() {}

	/**
	 * @param version
	 * @return
	 */
	public static LocalDate versionToLocalDate(Version version) {
		Entry<Version, LocalDate> lowerEntry = VERSION_DATE_MAP.lowerEntry(version);
		if(lowerEntry == null) {
			return VERSION_DATE_MAP.firstEntry().getValue();
		}
		return lowerEntry.getValue();
	}

	/**
	 * @param version
	 * @return
	 */
	public static Date versionToDate(Version version) {
		return Date.from(versionToLocalDate(version).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * @param build
	 * @return
	 */
	public static LocalDate buildToLocalDate(int build) {
		LocalDate localDate = BUILD_DATE_MAP.get(build);
		if(localDate != null) {
			return localDate;
		}
		Entry<Integer, LocalDate> lowerEntry = BUILD_DATE_MAP.lowerEntry(build);
		if(lowerEntry == null) {
			return BUILD_DATE_MAP.firstEntry().getValue();
		}
		return lowerEntry.getValue();
	}

	/**
	 * @param build
	 * @return
	 */
	public static Date buildToDate(int build) {
		return Date.from(buildToLocalDate(build).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	// *************************************************

	/**
	 * @return
	 */
	private static NavigableMap<Version, LocalDate> versionDateMap() {
		// from http://ffmpeg.org/releases/
		NavigableMap<Version, LocalDate> map = new TreeMap<>();
		map.put(new Version(4), LocalDate.of(2018, 4, 20));
		map.put(new Version(3, 4, 2), LocalDate.of(2018, 2, 12));
		map.put(new Version(3, 4, 1), LocalDate.of(2017, 12, 11));
		map.put(new Version(3, 4), LocalDate.of(2017, 10, 15));
		map.put(new Version(3, 3, 7), LocalDate.of(2018, 4, 14));
		map.put(new Version(3, 3, 6), LocalDate.of(2018, 1, 1));
		map.put(new Version(3, 3, 5), LocalDate.of(2017, 10, 26));
		map.put(new Version(3, 3, 4), LocalDate.of(2017, 9, 12));
		map.put(new Version(3, 3, 3), LocalDate.of(2017, 7, 29));
		map.put(new Version(3, 3, 2), LocalDate.of(2017, 6, 7));
		map.put(new Version(3, 3, 1), LocalDate.of(2017, 5, 15));
		// some 3.2.x
		map.put(new Version(3, 2, 2), LocalDate.of(2016, 12, 16));
		map.put(new Version(3, 2), LocalDate.of(2016, 11, 6));
		map.put(new Version(3, 1, 5), LocalDate.of(2016, 10, 24));
		map.put(new Version(3, 1, 4), LocalDate.of(2016, 10, 19));
		map.put(new Version(3, 0, 1), LocalDate.of(2016, 10, 19));
		map.put(new Version(3), LocalDate.of(2016, 2, 17));
		map.put(new Version(2, 8, 6), LocalDate.of(2016, 2, 3));
		map.put(new Version(2, 8, 5), LocalDate.of(2016, 1, 15));
		map.put(new Version(2, 8, 4), LocalDate.of(2015, 12, 23));
		map.put(new Version(2, 8, 3), LocalDate.of(2015, 12, 3));
		map.put(new Version(2, 8, 2), LocalDate.of(2015, 12, 3));
		map.put(new Version(2, 8, 1), LocalDate.of(2015, 10, 15));
		map.put(new Version(2, 8), LocalDate.of(2015, 10, 06));
		map.put(new Version(2, 7), LocalDate.of(2015, 6, 12));
		map.put(new Version(2, 5, 2), LocalDate.of(2014, 12, 30));
		map.put(new Version(2, 4, 5), LocalDate.of(2014, 12, 30));
		map.put(new Version(2, 2, 11), LocalDate.of(2014, 12, 30));
		map.put(new Version(1, 2, 11), LocalDate.of(2014, 12, 30));
		map.put(new Version(2, 2, 3), LocalDate.of(2014, 6, 19));
		map.put(new Version(2, 2, 2), LocalDate.of(2014, 5, 22));
		map.put(new Version(2, 2, 1), LocalDate.of(2014, 4, 10));
		map.put(new Version(2, 1, 4), LocalDate.of(2014, 2, 26));
		map.put(new Version(2, 1, 3), LocalDate.of(2014, 1, 21));
		map.put(new Version(2, 1, 1), LocalDate.of(2013, 11, 20));
		map.put(new Version(2, 1), LocalDate.of(2013, 10, 30));
		map.put(new Version(2, 0, 2), LocalDate.of(2013, 10, 26));
		map.put(new Version(2, 0, 1), LocalDate.of(2013, 9, 26));
		map.put(new Version(1, 2), LocalDate.of(2013, 3, 27));
		map.put(new Version(1, 1, 3), LocalDate.of(2013, 3, 03));
		map.put(new Version(1, 1, 1), LocalDate.of(2013, 1, 20));
		map.put(new Version(1, 1), LocalDate.of(2013, 1, 8));
		map.put(new Version(1, 0, 1), LocalDate.of(2013, 1, 2));
		map.put(new Version(0, 8), LocalDate.of(2011, 6, 23));
		map.put(new Version(0, 7, 1), LocalDate.of(2011, 6, 23));
		return map;
	}

	/**
	 * @return
	 */
	private static NavigableMap<Integer, LocalDate> buildDateMap() {
		NavigableMap<Integer, LocalDate> map = new TreeMap<>();
		map.put(90884, LocalDate.of(2018, 4, 29));
		// a gap...
		map.put(82966, LocalDate.of(2016, 12, 31));
		map.put(81863, LocalDate.of(2016, 10, 3));
		map.put(80877, LocalDate.of(2016, 6, 29));
		map.put(79056, LocalDate.of(2016, 3, 16));
		map.put(77556, LocalDate.of(2015, 12, 31));
		map.put(72939, LocalDate.of(2015, 6, 15));
		map.put(68810, LocalDate.of(2015, 1, 2));
		map.put(64326, LocalDate.of(2014, 6, 30));
		map.put(59480, LocalDate.of(2014, 1, 2));
		map.put(48409, LocalDate.of(2013, 1, 3));
		map.put(39664, LocalDate.of(2012, 4, 9));
		return map;
	}
}
