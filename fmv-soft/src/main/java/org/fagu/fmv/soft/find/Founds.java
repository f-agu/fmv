package org.fagu.fmv.soft.find;

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

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;

import org.fagu.fmv.soft.SoftName;


/**
 * @author f.agu
 */
public class Founds implements Iterable<SoftFound> {

	private final SoftName softName;

	private final NavigableSet<SoftFound> founds;

	private final SoftPolicy<?, ?, ?> softPolicy;

	/**
	 * @param softName
	 * @param founds
	 * @param softPolicy
	 */
	public Founds(SoftName softName, NavigableSet<SoftFound> founds, SoftPolicy<?, ?, ?> softPolicy) {
		this.softName = Objects.requireNonNull(softName);
		this.founds = Objects.requireNonNull(founds);
		this.softPolicy = softPolicy;
	}

	/**
	 * @return
	 */
	public SoftName getSoftName() {
		return softName;
	}

	/**
	 * @return
	 */
	public boolean isFound() {
		return founds.stream() //
				.map(f -> f.isFound()) //
				.filter(f -> f) //
				.findFirst() //
				.orElse(false);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return founds.isEmpty();
	}

	/**
	 * @return
	 */
	public SoftFound getFirstFound() {
		return getFirstFound(null);
	}

	/**
	 * @param defaultValue
	 * @return
	 */
	public SoftFound getFirstFound(SoftFound defaultValue) {
		return founds.descendingSet().stream()
				.filter(f -> f.isFound() && f.getFile().exists())
				.findFirst()
				.orElse(defaultValue);
	}

	/**
	 * @return
	 */
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return softPolicy;
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<SoftFound> iterator() {
		return founds.iterator();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return founds.toString();
	}

}
