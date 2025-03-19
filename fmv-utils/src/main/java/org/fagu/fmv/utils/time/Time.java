package org.fagu.fmv.utils.time;

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

import org.apache.commons.lang3.math.NumberUtils;


/**
 * @author f.agu
 */
public class Time extends AbstractTime implements Comparable<Time> {

	private static final Time T0 = new Time(0);

	private Time(double seconds) {
		super(seconds);
	}

	// *****************

	public static Time valueOf(double seconds) {
		if(seconds == 0) {
			return T0;
		}
		return new Time(seconds);
	}

	public Time(int hour, int minute, double second) {
		super(hour, minute, second);
	}

	public Time(int hour, int minute, double second, boolean negative) {
		super(hour, minute, second, negative);
	}

	public Time add(Duration duration) {
		return new Time(toSeconds() + duration.toSeconds());
	}

	@Override
	public int compareTo(Time o) {
		return super.compareTo(o);
	}

	public Duration diff(Time other) {
		return Duration.valueOf(Math.abs(other.toSeconds() - toSeconds()));
	}

	public boolean after(Time other) {
		return other.toSeconds() < toSeconds();
	}

	public boolean before(Time other) {
		return other.toSeconds() > toSeconds();
	}

	public static Time parse(String str) {
		Matcher matcher = PATTERN.matcher(str);
		if( ! matcher.matches()) {
			throw new IllegalArgumentException("Unable to parse: '" + str + '\'');
		}
		boolean negative = "-".equals(matcher.group(1));
		int hour = NumberUtils.toInt(matcher.group(4));
		int minute = NumberUtils.toInt(matcher.group(5));
		double second = NumberUtils.toDouble(matcher.group(6));
		return new Time(hour, minute, second, negative);
	}
}
