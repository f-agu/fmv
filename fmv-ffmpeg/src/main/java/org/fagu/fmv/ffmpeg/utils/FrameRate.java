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

import org.fagu.fmv.utils.Fractionable;
import org.fagu.fmv.utils.collection.MapList;
import org.fagu.fmv.utils.collection.MultiValueMaps;


/**
 * @author f.agu
 */
public class FrameRate extends Fractionable<FrameRate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 137029467904009288L;

	/**
	 * 
	 */
	private static final MapList<String, FrameRate> FRAME_MAP = MultiValueMaps.hashMapArrayList();

	/**
	 * 
	 */
	public static final FrameRate NTSC = new FrameRate("ntsc", 30000, 1001);

	/**
	 * 
	 */
	public static final FrameRate PAL = new FrameRate("pal", 25);

	/**
	 * 
	 */
	public static final FrameRate QNTSC = new FrameRate("qntsc", 30000, 1001);

	/**
	 * 
	 */
	public static final FrameRate QPAL = new FrameRate("qpal", 25);

	/**
	 * 
	 */
	public static final FrameRate SNTSC = new FrameRate("sntsc", 30000, 1001);

	/**
	 * 
	 */
	public static final FrameRate SPAL = new FrameRate("spal", 25);

	/**
	 * 
	 */
	public static final FrameRate FILM = new FrameRate("film", 24);

	/**
	 * 
	 */
	public static final FrameRate NTSC_FILM = new FrameRate("ntsc-film", 24000, 1001);

	/**
	 * 
	 */
	private String name;

	/**
	 * @param fraction
	 */
	protected FrameRate(org.apache.commons.lang3.math.Fraction fraction) {
		super(fraction);
	}

	/**
	 * @param numerator
	 * @param denominator
	 */
	protected FrameRate(int numerator, int denominator) {
		super(numerator, denominator);
		name = "custom";
	}

	/**
	 * @param name
	 * @param numerator
	 */
	protected FrameRate(String name, int numerator) {
		this(name, numerator, 1);
	}

	/**
	 * @param name
	 * @param numerator
	 * @param denominator
	 */
	protected FrameRate(String name, int numerator, int denominator) {
		super(numerator, denominator);
		this.name = name;
		FRAME_MAP.add(getKey(numerator, denominator), this);
	}

	/**
	 * @return
	 */
	public static FrameRate perSecond(int f) {
		return valueOf(f, 1);
	}

	/**
	 * @param perSeconds
	 * @return
	 */
	public static FrameRate valueOf(double perSeconds) {
		return valueOf(org.apache.commons.lang3.math.Fraction.getFraction(perSeconds));
	}

	/**
	 * @param fraction
	 * @return
	 */
	public static FrameRate valueOf(org.apache.commons.lang3.math.Fraction fraction) {
		return valueOf(fraction.getNumerator(), fraction.getDenominator());
	}

	/**
	 * @param numerator
	 * @param denominator
	 * @return
	 */
	public static FrameRate valueOf(int numerator, int denominator) {
		String key = getKey(numerator, denominator);
		FrameRate first = FRAME_MAP.getFirst(key);
		return first != null ? first : new FrameRate(numerator, denominator);
	}

	/**
	 * @param str
	 * @return
	 */
	public static FrameRate parse(String str) {
		Matcher matcher = FRACTION_PATTERN.matcher(str);
		if(matcher.matches()) {
			int denominator = Integer.parseInt(matcher.group(2));
			if(denominator == 0) {
				return null;
			}
			return valueOf(Integer.parseInt(matcher.group(1)), denominator);
		}
		matcher = DIGIT_PATTERN.matcher(str);
		if(matcher.matches()) {
			return valueOf(Integer.parseInt(str), 1);
		}
		throw new IllegalArgumentException(str);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	// *******************************************

	/**
	 * @see org.fagu.fmv.utils.Fractionable#create(int, int)
	 */
	@Override
	protected FrameRate create(int numerator, int denominator) {
		return valueOf(numerator, denominator);
	}

}
