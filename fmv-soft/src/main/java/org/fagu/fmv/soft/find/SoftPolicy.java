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
import org.fagu.fmv.soft.find.SoftPolicy.OnPlatform;


/**
 * @author f.agu
 */
@SuppressWarnings("rawtypes")
public abstract class SoftPolicy<S extends SoftInfo, P extends OnPlatform, T extends SoftPolicy<S, P, ?>> {

	protected final List<Pair<P, Predicate<S>>> list;

	// -------------------------------------------------

	/**
	 * @author f.agu
	 */
	public abstract class OnPlatform {

		protected final String name;

		protected final Predicate<S> condition;

		public OnPlatform(String name, Predicate<S> condition) {
			this.name = Objects.requireNonNull(name);
			this.condition = Objects.requireNonNull(condition);
		}

		public String getName() {
			return name;
		}

		public Predicate<S> getCondition() {
			return condition;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	// -------------------------------------------------

	/**
	 * 
	 */
	public SoftPolicy() {
		list = new ArrayList<>();
	}

	/**
	 * @param platform
	 * @param validate
	 * @return
	 */
	public T with(P platform, Predicate<S> validate) {
		Objects.requireNonNull(platform);
		Objects.requireNonNull(validate);
		list.add(Pair.of(platform, validate));
		return getThis();
	}

	/**
	 * @return
	 */
	public P onAllPlatforms() {
		return on("All platforms", s -> true);
	}

	/**
	 * @return
	 */
	public P onWindows() {
		return on("Windows", s -> SystemUtils.IS_OS_WINDOWS);
	}

	/**
	 * @return
	 */
	public P onLinux() {
		return on("Linux", s -> SystemUtils.IS_OS_LINUX);
	}

	/**
	 * @return
	 */
	public P onMac() {
		return on("Mac", s -> SystemUtils.IS_OS_MAC);
	}

	/**
	 * @param name
	 * @param condition
	 * @return
	 */
	public abstract P on(String name, Predicate<S> condition);

	/**
	 * @param softInfo
	 * @return
	 */
	public abstract SoftFound toSoftFound(Object softInfo);

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(" ; ");
		for(Pair<P, Predicate<S>> pair : list) {
			joiner.add(pair.getKey().toString() + '[' + pair.getValue() + ']');
		}
		return joiner.toString();
	}

	// **********************************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T getThis() {
		return (T)this;
	}

	/**
	 * @return
	 */
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

	/**
	 * @param softInfo
	 * @param name
	 * @return
	 */
	protected static Optional<String> getProperty(SoftInfo softInfo, String name) {
		// global
		String key = softInfo.getName() + "." + name;
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
