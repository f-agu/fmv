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

	public Founds(String softName, NavigableSet<SoftFound> founds, SoftPolicy softPolicy, Properties searchProperties) {
		this.softName = Objects.requireNonNull(softName);
		this.foundSet = Objects.requireNonNull(founds);
		this.softPolicy = softPolicy;
		this.searchProperties = ImmutableProperties.copyOf(searchProperties);
	}

	public String getSoftName() {
		return softName;
	}

	public NavigableSet<SoftFound> getFounds() {
		return foundSet;
	}

	public boolean isFound() {
		return foundSet.stream()
				.map(SoftFound::isFound)
				.filter(f -> f)
				.findFirst()
				.orElse(false);
	}

	public boolean isEmpty() {
		return foundSet.isEmpty();
	}

	public SoftFound getFirstFound() {
		return getFirstFound(null);
	}

	public SoftFound getFirstFound(SoftFound defaultValue) {
		return foundSet.descendingSet()
				.stream()
				.filter(f -> f.isFound() && f.getFile().exists())
				.findFirst()
				.orElse(defaultValue);
	}

	public SoftPolicy getSoftPolicy() {
		return softPolicy;
	}

	public Properties getSearchProperties() {
		return searchProperties;
	}

	@Override
	public Iterator<SoftFound> iterator() {
		return foundSet.iterator();
	}

	@Override
	public String toString() {
		return foundSet.toString();
	}

}
