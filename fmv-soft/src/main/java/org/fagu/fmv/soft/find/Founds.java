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
import java.util.Properties;

import org.fagu.fmv.soft.utils.ImmutableProperties;


/**
 * @author f.agu
 */
public class Founds implements Iterable<SoftFound> {

	private final String softName;

	private final NavigableSet<SoftFound> foundSet;

	private final SoftPolicy softPolicy;

	private final Properties searchProperties;

	/**
	 * @param softName
	 * @param founds
	 * @param softPolicy
	 * @param searchProperties
	 */
	public Founds(String softName, NavigableSet<SoftFound> founds, SoftPolicy softPolicy, Properties searchProperties) {
		this.softName = Objects.requireNonNull(softName);
		this.foundSet = Objects.requireNonNull(founds);
		this.softPolicy = softPolicy;
		this.searchProperties = ImmutableProperties.copyOf(searchProperties);
	}

	/**
	 * @return
	 */
	public String getSoftName() {
		return softName;
	}

	/**
	 * @return
	 */
	public boolean isFound() {
		return foundSet.stream()
				.map(SoftFound::isFound)
				.filter(f -> f)
				.findFirst()
				.orElse(false);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return foundSet.isEmpty();
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
		return foundSet.descendingSet()
				.stream()
				.filter(f -> f.isFound() && f.getFile().exists())
				.findFirst()
				.orElse(defaultValue);
	}

	/**
	 * @return
	 */
	public SoftPolicy getSoftPolicy() {
		return softPolicy;
	}

	/**
	 * @return
	 */
	public Properties getSearchProperties() {
		return searchProperties;
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<SoftFound> iterator() {
		return foundSet.iterator();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return foundSet.toString();
	}

}
