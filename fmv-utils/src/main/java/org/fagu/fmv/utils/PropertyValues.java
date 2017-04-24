package org.fagu.fmv.utils;

import java.util.Map;

/*
 * #%L
 * fmv-core
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

import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class PropertyValues {

	/**
	 * @author f.agu
	 */
	public static class BooleanPropertyValue extends AbstractPropertyValue<Boolean> {

		/**
		 * @param name
		 */
		public BooleanPropertyValue(String name) {
			this(name, false);
		}

		/**
		 * @param name
		 * @param defaultValue
		 */
		public BooleanPropertyValue(String name, Boolean defaultValue) {
			super(name, defaultValue);
		}

		/**
		 * @see org.fagu.fmv.core.project.PropertyValue#fromValue(java.lang.Object)
		 */
		@Override
		public String fromValue(Boolean v) {
			return v.toString();
		}

		/**
		 * @see org.fagu.fmv.core.project.PropertyValue#toValue(java.lang.String)
		 */
		@Override
		public Boolean toValue(String str) {
			if("0".equals(str)) {
				return false;
			} else if("1".equals(str)) {
				return true;
			}
			return BooleanUtils.toBoolean(str);
		}

	}

	// -------------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class IntegerPropertyValue extends AbstractPropertyValue<Integer> {

		/**
		 * @param name
		 */
		public IntegerPropertyValue(String name) {
			super(name, 0);
		}

		/**
		 * @param name
		 * @param defaultValue
		 */
		public IntegerPropertyValue(String name, Integer defaultValue) {
			super(name, defaultValue);
		}

		/**
		 * @see org.fagu.fmv.core.project.PropertyValue#fromValue(java.lang.Object)
		 */
		@Override
		public String fromValue(Integer v) {
			return v.toString();
		}

		/**
		 * @see org.fagu.fmv.core.project.PropertyValue#toValue(java.lang.String)
		 */
		@Override
		public Integer toValue(String str) {
			return Integer.parseInt(str);
		}

	}

	// -------------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class StringPropertyValue extends AbstractPropertyValue<String> {

		/**
		 * @param name
		 */
		public StringPropertyValue(String name) {
			super(name, StringUtils.EMPTY);
		}

		/**
		 * @param name
		 * @param defaultValue
		 */
		public StringPropertyValue(String name, String defaultValue) {
			super(name, defaultValue);
		}

		/**
		 * @see org.fagu.fmv.core.project.PropertyValue#fromValue(java.lang.Object)
		 */
		@Override
		public String fromValue(String v) {
			return v.toString();
		}

		/**
		 * @see org.fagu.fmv.core.project.PropertyValue#toValue(java.lang.String)
		 */
		@Override
		public String toValue(String str) {
			return String.valueOf(str); // for null
		}

	}

	// -------------------------------------------------------------------

	/**
	 * @author f.agu
	 *
	 * @param <V>
	 */
	public static abstract class AbstractPropertyValue<V> implements PropertyValue<V> {

		private final String name;

		private final V defaultValue;

		/**
		 * @param name
		 */
		public AbstractPropertyValue(String name) {
			this.name = Objects.requireNonNull(name);
			defaultValue = null;
		}

		/**
		 * @param name
		 * @param defaultValue
		 */
		public AbstractPropertyValue(String name, V defaultValue) {
			this.name = Objects.requireNonNull(name);
			this.defaultValue = defaultValue;
		}

		/**
		 * @see org.fagu.fmv.core.project.PropertyValue#name()
		 */
		@Override
		public String name() {
			return name;
		}

		/**
		 * @return
		 */
		@Override
		public V getDefaultValue() {
			return defaultValue;
		}

	}

	// -------------------------------------------------------------------

	private final Map<String, String> map;

	/**
	 * @param map
	 */
	public PropertyValues(Map<String, String> map) {
		this.map = Objects.requireNonNull(map);
	}

	/**
	 * @return
	 */
	public static PropertyValues fromSystemProperties() {
		@SuppressWarnings({"rawtypes", "unchecked"})
		Map<String, String> map = (Map)System.getProperties();
		return new PropertyValues(map);
	}

	/**
	 * @param property
	 * @return
	 */
	public static <V> V fromSystemProperties(PropertyValue<V> property) {
		return fromSystemProperties().getProperty(property);
	}

	/**
	 * @return
	 */
	public static PropertyValues fromSystemEnv() {
		return new PropertyValues(System.getenv());
	}

	/**
	 * @param property
	 * @return
	 */
	public static <V> V fromSystemEnv(PropertyValue<V> property) {
		return fromSystemEnv().getProperty(property);
	}

	/**
	 * @param property
	 * @return
	 */
	public <V> V getProperty(PropertyValue<V> property) {
		String name = property.name();
		if(map.containsKey(name)) {
			return property.toValue(map.get(name));
		}
		return property.getDefaultValue();
	}

}
