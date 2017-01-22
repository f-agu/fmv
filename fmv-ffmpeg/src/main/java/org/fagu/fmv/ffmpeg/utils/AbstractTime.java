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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import org.fagu.fmv.utils.NumberUtils;


/**
 * @author f.agu
 */
public abstract class AbstractTime {

	protected static final Pattern PATTERN = Pattern.compile("(\\+|-)?(((\\d+):)?(\\d+):)?((\\d+)(\\.\\d+)?)");

	private int hour;

	private int minute;

	private double second;

	private boolean negative;

	/**
	 * @param seconds
	 */
	public AbstractTime(double seconds) {
		init(Math.abs(seconds));
		negative = Math.signum(seconds) < 0;
	}

	/**
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public AbstractTime(int hour, int minute, double second) {
		this(hour, minute, second, false);
	}

	/**
	 * @param hour
	 * @param minute
	 * @param second
	 * @param negative
	 */
	public AbstractTime(int hour, int minute, double second, boolean negative) {
		if(hour < 0) {
			throw new IllegalArgumentException("hour negative: " + hour);
		}
		if(minute < 0) {
			throw new IllegalArgumentException("minute negative: " + hour);
		}
		if(second < 0) {
			throw new IllegalArgumentException("second negative: " + hour);
		}
		this.negative = negative;
		init(toSeconds(hour, minute, second, false));
	}

	/**
	 * @return the negative
	 */
	public boolean isNegative() {
		return negative;
	}

	/**
	 * @return
	 */
	public int hour() {
		return hour;
	}

	/**
	 * @return
	 */
	public int minute() {
		return minute;
	}

	/**
	 * @return
	 */
	public double second() {
		return second;
	}

	/**
	 * @return
	 */
	public double toSeconds() {
		double d = toSeconds(hour, minute, second, negative);
		return Math.floor(d * 10000D) / 10000D;
	}

	/**
	 * @return
	 */
	@Override
	public int hashCode() {
		long bits = Double.doubleToLongBits(toSeconds());
		return (int)(bits ^ (bits >>> 32));
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof AbstractTime)) {
			return false;
		}
		AbstractTime other = (AbstractTime)obj;
		return negative == other.negative && hour == other.hour && minute == other.minute && NumberUtils.equals(second, other.second, 0.0001F);
	}

	/**
	 * @param other
	 * @return
	 */
	public int compareTo(AbstractTime other) {
		return Double.compare(toSeconds(), other.toSeconds());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(16);
		if(negative) {
			buf.append('-');
		}
		buf.append(MessageFormat.format("{0,number,00}:{1,number,00}:", hour, minute));
		DecimalFormat decimalFormat = new DecimalFormat("00.000", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		decimalFormat.setGroupingUsed(false);
		decimalFormat.setMaximumFractionDigits(3);
		decimalFormat.setMinimumFractionDigits(3);
		buf.append(decimalFormat.format(second));
		return buf.toString();
	}

	/**
	 * @param hour
	 * @param minute
	 * @param second
	 * @param negative
	 * @return
	 */
	public static double toSeconds(int hour, int minute, double second, boolean negative) {
		return (negative ? - 1D : 1D) * (60D * ((60D * hour) + minute) + second);
	}

	// *****************************************************

	/**
	 * @param abstractTime
	 * @return
	 */
	protected boolean equals(AbstractTime abstractTime) {
		if(abstractTime == null) {
			return false;
		}
		return hour == abstractTime.hour && minute == abstractTime.minute && second == abstractTime.second;
	}

	// *****************************************************

	/**
	 * @param second
	 */
	private void init(double seconds) {
		int s = (int)seconds;
		second = s % 60 + seconds - Math.floor(seconds);
		minute = (s / 60) % 60;
		hour = s / 3600;
		if(seconds == 0.0) {
			negative = false;
		}
	}

}
