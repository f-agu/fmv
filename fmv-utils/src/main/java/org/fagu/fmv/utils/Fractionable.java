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

import java.util.regex.Pattern;

import org.apache.commons.lang3.math.Fraction;


/**
 * @author f.agu
 */
public abstract class Fractionable<F extends Fractionable<F>> extends Number implements Comparable<F> {

	private static final long serialVersionUID = - 9146292854597227317L;

	public static final Pattern FRACTION_PATTERN = Pattern.compile("(\\d+)/(\\d+)");

	public static final Pattern DIGIT_PATTERN = Pattern.compile("\\d+");

	private final Fraction fraction;

	protected Fractionable(Fraction fraction) {
		this.fraction = fraction;
	}

	protected Fractionable(int numerator, int denominator) {
		this(Fraction.getFraction(numerator, denominator));
	}

	public int getNumerator() {
		return fraction.getNumerator();
	}

	public int getDenominator() {
		return fraction.getDenominator();
	}

	public int getProperNumerator() {
		return fraction.getProperNumerator();
	}

	public int getProperWhole() {
		return fraction.getProperWhole();
	}

	public F reduce() {
		return create(fraction.reduce());
	}

	public F invert() {
		return create(fraction.invert());
	}

	public F negate() {
		return create(fraction.negate());
	}

	public F abs() {
		return create(fraction.abs());
	}

	public F pow(int power) {
		return create(fraction.pow(power));
	}

	@Override
	public int intValue() {
		return fraction.intValue();
	}

	@Override
	public long longValue() {
		return fraction.longValue();
	}

	@Override
	public float floatValue() {
		return fraction.floatValue();
	}

	@Override
	public double doubleValue() {
		return fraction.doubleValue();
	}

	public F multiplyBy(F f) {
		return create(fraction.multiplyBy(f.toFraction()));
	}

	/**
	 * <p>
	 * Divide the value of this fraction by another.
	 * </p>
	 *
	 * @param fraction the fraction to divide by, must not be <code>null</code>
	 * @return a <code>Fraction</code> instance with the resulting values
	 * @throws IllegalArgumentException if the fraction is <code>null</code>
	 * @throws ArithmeticException if the fraction to divide by is zero
	 * @throws ArithmeticException if the resulting numerator or denominator exceeds <code>Integer.MAX_VALUE</code>
	 */
	public F divideBy(F f) {
		return create(fraction.divideBy(f.toFraction()));
	}

	@Override
	public int compareTo(F other) {
		return fraction.compareTo(other.toFraction());
	}

	public Fraction toFraction() {
		return fraction;
	}

	@Override
	public boolean equals(final Object obj) {
		if(obj == this) {
			return true;
		}
		if( ! (obj instanceof Fractionable)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Fractionable<F> other = (Fractionable<F>)obj;
		return fraction.equals(other.toFraction());
	}

	@Override
	public int hashCode() {
		return fraction.hashCode();
	}

	@Override
	public String toString() {
		int numerator = fraction.getNumerator();
		int denominator = fraction.getDenominator();
		if(denominator == 1) {
			return Integer.toString(numerator);
		}
		return getKey(numerator, denominator);
	}

	// ****************************************************

	protected abstract F create(int numerator, int denominator);

	// ****************************************************

	protected F create(Fraction fraction) {
		return create(fraction.getNumerator(), fraction.getDenominator());
	}

	protected F preFunction() {
		return null;
	}

	protected F createZero() {
		return create(0, 1);
	}

	@SuppressWarnings("unchecked")
	protected F getFThis() {
		return (F)this;
	}

	protected static String getKey(int numerator, int denominator) {
		return new StringBuilder().append(numerator).append('/').append(denominator).toString();
	}

}
