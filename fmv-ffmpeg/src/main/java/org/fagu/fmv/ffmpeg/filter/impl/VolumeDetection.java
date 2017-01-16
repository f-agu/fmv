package org.fagu.fmv.ffmpeg.filter.impl;

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

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;


/**
 * @author f.agu
 */
class VolumeDetection {

	private static final Pattern LOG_PATTERN = Pattern.compile("([a-zA-Z0-9_-]+): ([-]?[0-9\\\\.]+)( dB)?");

	private static final Pattern HISTOGRAM_PATTERN = Pattern.compile("histogram_+([0-9]+)db");

	private Long countSample;

	private Float mean, max;

	private final SortedMap<Integer, Long> histogram;

	/**
	 *
	 */
	VolumeDetection() {
		histogram = new TreeMap<>();
	}

	/**
	 * @param log
	 */
	void add(String log) {
		Matcher matcher = LOG_PATTERN.matcher(log);
		if(matcher.matches()) {
			// for(int i = 0; i <= matcher.groupCount(); i++) {
			// System.out.println(i + " : " + matcher.group(i));
			// }
			String key = matcher.group(1);
			String value = matcher.group(2);

			if("n_samples".equals(key)) {
				countSample = toLong(value);
			} else if("mean_volume".equals(key)) {
				mean = toFloat(value);
			} else if("max_volume".equals(key)) {
				max = toFloat(value);
			} else if(key.startsWith("histogram_")) {
				Matcher histMatcher = HISTOGRAM_PATTERN.matcher(key);
				if(histMatcher.matches()) {
					int db = NumberUtils.toInt(histMatcher.group(1), Integer.MIN_VALUE);
					Long l = toLong(value);
					if(db != Integer.MIN_VALUE && l != null) {
						histogram.put(db, l);
					}
				}
			}
		}
	}

	/**
	 * @return
	 */
	boolean isDetected() {
		return countSample != null && mean != null && max != null;
	}

	/**
	 * @return
	 */
	VolumeDetected getDetected() {
		if( ! isDetected()) {
			throw new RuntimeException("not detected");
		}
		return new VolumeDetected(countSample, mean, max, histogram);
	}

	// ************************************************

	/**
	 * @param str
	 * @return
	 */
	private static Long toLong(String str) {
		if(str == null) {
			return null;
		}
		try {
			return Long.parseLong(str);
		} catch(final NumberFormatException nfe) {
			return null;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	private static Float toFloat(String str) {
		if(str == null) {
			return null;
		}
		try {
			return Float.parseFloat(str);
		} catch(final NumberFormatException nfe) {
			return null;
		}
	}
}
