package org.fagu.fmv.soft.find;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Predicate;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;


/**
 * @author f.agu
 */
public abstract class SoftPolicy {

	protected final List<Pair<OnPlatform, Predicate<SoftInfo>>> list;

	private Sorter sorter;

	// -------------------------------------------------

	/**
	 * @author f.agu
	 */
	public class OnPlatform {

		protected final String name;

		protected final Predicate<SoftInfo> require;

		public OnPlatform(String name, Predicate<SoftInfo> require) {
			this.name = Objects.requireNonNull(name);
			this.require = Objects.requireNonNull(require);
		}

		public String getName() {
			return name;
		}

		public Predicate<SoftInfo> getRequire() {
			return require;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	// -------------------------------------------------

	protected SoftPolicy() {
		list = new ArrayList<>();
	}

	public SoftPolicy with(OnPlatform platform, Predicate<SoftInfo> validate) {
		Objects.requireNonNull(platform);
		Objects.requireNonNull(validate);
		list.add(Pair.of(platform, validate));
		return this;
	}

	public SoftPolicy on(String platformName, Predicate<SoftInfo> platformPredicate, Predicate<SoftInfo> subPredicate) {
		return with(new OnPlatform(platformName, platformPredicate), subPredicate);
	}

	public SoftPolicy withSorter(Sorter sorter) {
		this.sorter = sorter;
		return this;
	}

	public SoftPolicy onAllPlatforms(Predicate<SoftInfo> subPredicate) {
		return on("All platforms", s -> true, subPredicate);
	}

	public SoftPolicy onWindows(Predicate<SoftInfo> subPredicate) {
		return on("Windows", s -> SystemUtils.IS_OS_WINDOWS, subPredicate);
	}

	public SoftPolicy onLinux(Predicate<SoftInfo> subPredicate) {
		return on("Linux", s -> SystemUtils.IS_OS_LINUX, subPredicate);
	}

	public SoftPolicy onMac(Predicate<SoftInfo> subPredicate) {
		return on("Mac", s -> SystemUtils.IS_OS_MAC, subPredicate);
	}

	public abstract SoftFound toSoftFound(Object softInfo);

	public Sorter getSorter() {
		return sorter;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(" ; ");
		for(Pair<OnPlatform, Predicate<SoftInfo>> pair : list) {
			joiner.add(pair.getKey().toString() + '[' + pair.getValue() + ']');
		}
		return joiner.toString();
	}

	// **********************************************************

	protected static String osCode() {
		if(SystemUtils.IS_OS_WINDOWS) {
			return "windows";
		}
		if(SystemUtils.IS_OS_LINUX) {
			return "linux";
		}
		if(SystemUtils.IS_OS_WINDOWS) {
			return "mac";
		}
		return null;
	}

	protected static Optional<String> getProperty(SoftInfo softInfo, String name) {
		// global
		String key = softInfo.getName() + '.' + name;
		String property = System.getProperty("fmv." + key, System.getProperty(key));
		if(property == null) {
			// by OS
			String osCode = osCode();
			if(osCode == null) {
				return Optional.empty();
			}
			key += "." + osCode;
			property = System.getProperty("fmv." + key, System.getProperty(key));
		}
		return Optional.ofNullable(property);
	}

}
