package org.fagu.fmv.ffmpeg.utils;

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

import java.util.regex.Matcher;

import org.apache.commons.lang.math.NumberUtils;


/**
 * @author f.agu
 */
public class Duration extends AbstractTime implements Comparable<Duration> {

	private static final Duration UNLIMITED = new Duration(Double.POSITIVE_INFINITY) {

		@Override
		public int hour() {
			return 0;
		}

		@Override
		public int minute() {
			return 0;
		}

		@Override
		public double second() {
			return 0;
		}

		@Override
		public double toSeconds() {
			return Double.POSITIVE_INFINITY;
		}

		@Override
		public Duration add(double seconds) {
			return UNLIMITED;
		}

		@Override
		public Duration add(Duration duration) {
			return UNLIMITED;
		}

		@Override
		public Duration multiplyBy(double multiple) {
			return UNLIMITED;
		}

		@Override
		public String toString() {
			return "unlimited";
		}
	};

	/**
	 * @param seconds
	 */
	private Duration(double seconds) {
		super(seconds);
	}

	// *****************

	/**
	 * @return
	 */
	public static Duration unlimited() {
		return UNLIMITED;
	}

	/**
	 * @param seconds
	 * @return
	 */
	public static Duration valueOf(double seconds) {
		return new Duration(seconds);
	}

	/**
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public Duration(int hour, int minute, double second) {
		super(hour, minute, second);
	}

	/**
	 * @param hour
	 * @param minute
	 * @param second
	 * @param negative
	 */
	public Duration(int hour, int minute, double second, boolean negative) {
		super(hour, minute, second, negative);
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Duration o) {
		return super.compareTo(o);
	}

	/**
	 * @param duration
	 * @return
	 */
	public Duration add(Duration duration) {
		return add(duration.toSeconds());
	}

	/**
	 * @param duration
	 * @return
	 */
	public Duration add(double seconds) {
		return Duration.valueOf(toSeconds() + seconds);
	}

	/**
	 * @param multiple
	 * @return
	 */
	public Duration multiplyBy(double multiple) {
		return valueOf(toSeconds() * multiple);
	}

	/**
	 * @param str
	 * @return
	 */
	public static Duration parse(String str) {
		Matcher matcher = PATTERN.matcher(str);
		if( ! matcher.matches()) {
			throw new IllegalArgumentException("Unable to parse: '" + str + '\'');
		}
		boolean negative = "-".equals(matcher.group(1));
		int hour = NumberUtils.toInt(matcher.group(4));
		int minute = NumberUtils.toInt(matcher.group(5));
		double second = NumberUtils.toDouble(matcher.group(6));
		return new Duration(hour, minute, second, negative);
	}

}
