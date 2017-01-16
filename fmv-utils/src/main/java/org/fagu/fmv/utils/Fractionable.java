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

	/**
	 * @param fraction
	 */
	protected Fractionable(Fraction fraction) {
		this.fraction = fraction;
	}

	/**
	 * @param numerator
	 * @param denominator
	 */
	protected Fractionable(int numerator, int denominator) {
		this(Fraction.getFraction(numerator, denominator));
	}

	/**
	 * @return
	 */
	public int getNumerator() {
		return fraction.getNumerator();
	}

	/**
	 * @return
	 */
	public int getDenominator() {
		return fraction.getDenominator();
	}

	/**
	 * @return
	 */
	public int getProperNumerator() {
		return fraction.getProperNumerator();
	}

	/**
	 * @return
	 */
	public int getProperWhole() {
		return fraction.getProperWhole();
	}

	/**
	 * @return
	 */
	public F reduce() {
		return create(fraction.reduce());
	}

	/**
	 * @return
	 */
	public F invert() {
		return create(fraction.invert());
	}

	/**
	 * @return
	 */
	public F negate() {
		return create(fraction.negate());
	}

	/**
	 * @return
	 */
	public F abs() {
		return create(fraction.abs());
	}

	/**
	 * @param power
	 * @return
	 */
	public F pow(int power) {
		return create(fraction.pow(power));
	}

	/**
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {
		return fraction.intValue();
	}

	/**
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue() {
		return fraction.longValue();
	}

	/**
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue() {
		return fraction.floatValue();
	}

	/**
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue() {
		return fraction.doubleValue();
	}

	/**
	 * @param fraction
	 * @return
	 */
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

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(F other) {
		return fraction.compareTo(other.toFraction());
	}

	/**
	 * @return
	 */
	public Fraction toFraction() {
		return fraction;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return fraction.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
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

	/**
	 * @param numerator
	 * @param denominator
	 * @return
	 */
	abstract protected F create(int numerator, int denominator);

	// ****************************************************

	/**
	 * @param numerator
	 * @param denominator
	 * @return
	 */
	protected F create(Fraction fraction) {
		return create(fraction.getNumerator(), fraction.getDenominator());
	}

	/**
	 * @return
	 */
	protected F preFunction() {
		return null;
	}

	/**
	 * @return
	 */
	protected F createZero() {
		return create(0, 1);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected F getFThis() {
		return (F)this;
	}

	/**
	 * @param numerator
	 * @param denominator
	 * @return
	 */
	protected static String getKey(int numerator, int denominator) {
		return new StringBuilder().append(numerator).append('/').append(denominator).toString();
	}

}
