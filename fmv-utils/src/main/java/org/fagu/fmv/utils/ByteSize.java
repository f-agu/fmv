package org.fagu.fmv.utils;

/*
 * #%L
 * fmv-utils
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

import static java.lang.Math.abs;
import static java.lang.Math.log;
import static java.lang.Math.log10;
import static java.lang.Math.pow;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * @author f.agu
 */
public class ByteSize {

	private static final Map<Locale, String[]> UNITS_MAP = new HashMap<>(4);

	static {
		UNITS_MAP.put(Locale.FRENCH, new String[] {"o", "ko", "Mo", "Go", "To", "Po", "Eo"});
		UNITS_MAP.put(Locale.ENGLISH, new String[] {"B", "kB", "MB", "GB", "TB", "PB", "EB"});
	}

	/**
	 * 
	 */
	private ByteSize() {
		//
	}

	/**
	 * @param size
	 * @return
	 */
	public static String formatSize(long size) {
		return formatSize(size, Locale.getDefault());
	}

	/**
	 * @param size
	 * @param locale
	 * @return
	 */
	public static String formatSize(long size, Locale locale) {
		String[] units = UNITS_MAP.get(locale);
		if(units == null) {
			units = UNITS_MAP.get(Locale.FRENCH);
		}
		DecimalFormat formatter = new DecimalFormat();
		if(locale != null) {
			formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
		}
		StringBuilder buf = new StringBuilder();
		double value;
		int unit;
		if(size == 0) {
			value = 0;
			unit = 1;
		} else {
			long absSize = abs(size);
			unit = (int)(log(absSize) / log(1024));
			if(unit < 0) {
				return "?";
			}
			value = absSize / pow(1024, unit);
			if(size < 0) {
				buf.append('-');
			}
			formatter.setMaximumFractionDigits(2 - (int)log10(value));
		}
		buf.append(formatter.format(value)).append(' ').append(units[unit]);
		return buf.toString();
	}
}
