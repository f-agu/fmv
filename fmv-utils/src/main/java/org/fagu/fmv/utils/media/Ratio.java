package org.fagu.fmv.utils.media;

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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.utils.Fractionable;
import org.fagu.fmv.utils.NumberUtils;


/**
 * @author f.agu
 */
public class Ratio extends Fractionable<Ratio> implements Comparable<Ratio>, Serializable {

	private static final long serialVersionUID = - 4421739062730069274L;

	private static final Pattern RATIO_SEP_PATTERN = Pattern.compile("([\\d\\.]+)(?::|/)([\\d\\.]+)");

	private static final Pattern RATIO_PATTERN = Pattern.compile("[\\d\\.]+");

	private static final Map<String, Ratio> RATIO_ND_MAP = new HashMap<>(16);

	private static final Map<Double, Ratio> RATIO_VALUE_MAP = new HashMap<>(16);

	public static final Ratio ZERO = new Ratio(0, 1);

	public static final Ratio TRADITIONAL_TELEVISION = new Ratio(4, 3);

	public static final Ratio ACADEMY = new Ratio(11, 8);

	public static final Ratio NTSC_FILM = new Ratio(22, 15);

	public static final Ratio ISO_216 = new Ratio(Math.sqrt(2));

	public static final Ratio IMAX = new Ratio(140, 97); // 70mm x 48.5mm

	public static final Ratio _35MM = new Ratio(3, 2);

	public static final Ratio _16MM = new Ratio(5, 3);

	public static final Ratio GOLDEN = new Ratio((1 + Math.sqrt(5)) / 2);

	public static final Ratio COMMON_COMPUTER_SCREEN = new Ratio(16, 10);

	public static final Ratio _16_9 = new Ratio(16, 9);

	public static final Ratio US_WIDESCREEN_CINEMA_STANDARD = new Ratio(37, 20);

	public static final Ratio WIDESCREEN_CINEMA_STANDARD_2_39 = new Ratio(239, 100);

	public static final Ratio WIDESCREEN_CINEMA_STANDARD_2_40 = new Ratio(12, 5);

	private final boolean isFraction;

	private final double value;

	private int countFractionDigits;

	private final boolean custom;

	private Ratio(double ratio) {
		this(ratio, false);
	}

	private Ratio(int numerator, int denominator) {
		this(numerator, denominator, false);
	}

	protected Ratio(double ratio, boolean custom) {
		super( - 1, - 1);
		this.value = ratio;
		this.custom = custom;
		isFraction = false;
		countFractionDigits = 3;

		addMe(getKey(getNumerator(), getDenominator(), ratio));
	}

	protected Ratio(int numerator, int denominator, boolean custom) {
		super(numerator, denominator);
		if(numerator < 0) {
			throw new IllegalArgumentException("Numerator must be positive");
		}
		if(denominator <= 0) {
			throw new IllegalArgumentException("Denominator must be positive");
		}
		this.value = (float)numerator / (float)denominator;
		this.custom = custom;
		isFraction = true;
		countFractionDigits = 0;

		addMe(getKey(numerator, denominator, value));
	}

	public static Ratio valueOf(int numerator, int denominator) {
		double r = (float)numerator / (float)denominator;
		String key = getKey(numerator, denominator, r);
		Ratio ratio = RATIO_ND_MAP.get(key);
		if(ratio != null) {
			return ratio;
		}
		return new Ratio(numerator, denominator, true);
	}

	public static Ratio valueOf(double ratioValue) {
		Ratio ratio = RATIO_VALUE_MAP.get(ratioValue);
		return ratio != null ? ratio : new Ratio(ratioValue, true);
	}

	public static Ratio parse(String str) {
		// a:b or a/b
		Matcher matcher = RATIO_SEP_PATTERN.matcher(str);
		if(matcher.matches()) {
			String n = matcher.group(1);
			String d = matcher.group(2);
			if(n.contains(".") || d.contains(".")) {
				double dn = Double.parseDouble(n);
				double dd = Double.parseDouble(d);
				return valueOf(dn / dd);
			}
			int in = Integer.valueOf(n);
			int id = Integer.valueOf(d);
			return valueOf(in, id);
		}

		// double
		matcher = RATIO_PATTERN.matcher(str);
		if(matcher.matches()) {
			return valueOf(Double.parseDouble(str));
		}

		throw new IllegalArgumentException("Unable to parse '" + str + '\'');
	}

	public Ratio approximate() {
		return approximate(0.04);
	}

	public Ratio approximate(double delta) {
		if(delta >= 1 || delta <= 0) {
			throw new IllegalArgumentException("Delta must be between 0 and 1 (both excluded)");
		}
		Optional<Ratio> first = RATIO_ND_MAP.values().stream()//
				.filter(r -> NumberUtils.equals(this.toDouble(), r.toDouble(), delta))//
				.findFirst();

		if(first.isPresent()) {
			return first.get();
		}

		int count = (int)Math.ceil( - Math.log10(delta));
		Ratio r = valueOf(NumberUtils.round(toDouble(), count));
		r.countFractionDigits = count;
		return r;
	}

	public int calculateWidth(Size size) {
		return calculateWidth(size.getHeight());
	}

	public int calculateWidth(int height) {
		if(isFraction) {
			return height * getNumerator() / getDenominator();
		}
		return (int)(height * toDouble());
	}

	public int calculateHeight(Size size) {
		return calculateHeight(size.getWidth());
	}

	public int calculateHeight(int width) {
		if(isFraction) {
			return width * getDenominator() / getNumerator();
		}
		return (int)(width / toDouble());
	}

	public Size getSizeByHeight(int height) {
		return Size.valueOf(calculateWidth(height), height);
	}

	public Size getSizeByWidth(int width) {
		return Size.valueOf(width, calculateHeight(width));
	}

	public boolean isVertical() {
		return toDouble() < 1D;
	}

	public boolean isHorizontal() {
		return toDouble() > 1D;
	}

	public boolean isSquare() {
		return Double.compare(toDouble(), 1D) == 0;
	}

	public double toDouble() {
		return value;
	}

	@Override
	public Ratio invert() {
		if(isFraction) {
			return super.invert();
		}
		return Ratio.valueOf(1 / value);
	}

	/**
	 * @see org.fagu.fmv.utils.Fractionable#reduce()
	 */
	@Override
	public Ratio reduce() {
		if(isFraction) {
			return super.reduce();
		}
		return this;
	}

	/**
	 * @see org.fagu.fmv.utils.Fractionable#negate()
	 */
	@Override
	public Ratio negate() {
		throw new RuntimeException("Are you crazy ?");
	}

	/**
	 * @see org.fagu.fmv.utils.Fractionable#abs()
	 */
	@Override
	public Ratio abs() {
		return this;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Ratio other) {
		return Double.compare(toDouble(), other.toDouble());
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof Ratio)) {
			return false;
		}
		Ratio other = (Ratio)obj;
		return super.equals(obj) && NumberUtils.equals(value, other.value, 0.00001);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * @return
	 */
	public String toFractionOrDouble() {
		if(isFraction) {
			return new StringBuilder().append(getNumerator()).append('/').append(getDenominator()).toString();
		}
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMinimumFractionDigits(countFractionDigits);
		decimalFormat.setMaximumFractionDigits(countFractionDigits);
		return decimalFormat.format(value);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(isFraction) {
			return new StringBuilder().append(getNumerator()).append(':').append(getDenominator()).toString();
		}
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMinimumFractionDigits(countFractionDigits);
		decimalFormat.setMaximumFractionDigits(countFractionDigits);
		return new StringBuilder().append(decimalFormat.format(value)).append(":1").toString();
	}

	// *****************************************

	@Override
	protected Ratio create(int numerator, int denominator) {
		return valueOf(numerator, denominator);
	}

	// *****************************************

	private void addMe(String key) {
		if( ! custom) {
			RATIO_ND_MAP.put(key, this);
			RATIO_VALUE_MAP.put(toDouble(), this);
		}
	}

	private static String getKey(int numerator, int denominator, double ratio) {
		return new StringBuilder(20).append(numerator).append(':').append(denominator).append('_').append(ratio).toString();
	}

}
