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


/**
 * @author f.agu
 */
public class Fraction extends Fractionable<Fraction> {

	private static final long serialVersionUID = - 9079925093366839973L;

	public Fraction(org.apache.commons.lang3.math.Fraction fraction) {
		super(fraction);
	}

	public Fraction(int numerator, int denominator) {
		super(numerator, denominator);
	}

	public static Fraction parse(String s) {
		Matcher matcher = FRACTION_PATTERN.matcher(s);
		if(matcher.matches()) {
			return new Fraction(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
		}
		matcher = DIGIT_PATTERN.matcher(s);
		if(matcher.matches()) {
			return new Fraction(Integer.parseInt(s), 1);
		}
		throw new IllegalArgumentException("Unable to parse: '" + s + '\'');
	}

	// *******************************************

	@Override
	protected Fraction create(int numerator, int denominator) {
		return new Fraction(numerator, denominator);
	}
}
