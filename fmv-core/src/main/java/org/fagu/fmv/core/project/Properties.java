package org.fagu.fmv.core.project;

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
public class Properties {

	/**
	 *
	 */
	public static final Property<Boolean> PREPARE_BACKGROUND = new BooleanProperty("prepare.background") {

		/**
		 * @see org.fagu.fmv.core.project.Properties.BooleanProperty#getDefaultValue()
		 */
		@Override
		public Boolean getDefaultValue() {
			return true;
		}
	};

	/**
	 *
	 */
	public static final Property<Boolean> SHOW_COMMAND_LINE = new BooleanProperty("commandline.show") {

		/**
		 * @see org.fagu.fmv.core.project.Properties.BooleanProperty#getDefaultValue()
		 */
		@Override
		public Boolean getDefaultValue() {
			return false;
		}
	};

	// -------------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class BooleanProperty extends AbstractProperty<Boolean> {

		/**
		 * @param name
		 */
		public BooleanProperty(String name) {
			super(name);
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#getDefaultValue()
		 */
		@Override
		public Boolean getDefaultValue() {
			return false;
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#fromValue(java.lang.Object)
		 */
		@Override
		public String fromValue(Boolean v) {
			return v.toString();
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#toValue(java.lang.String)
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
	public static class IntegerProperty extends AbstractProperty<Integer> {

		/**
		 * @param name
		 */
		public IntegerProperty(String name) {
			super(name);
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#getDefaultValue()
		 */
		@Override
		public Integer getDefaultValue() {
			return 0;
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#fromValue(java.lang.Object)
		 */
		@Override
		public String fromValue(Integer v) {
			return v.toString();
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#toValue(java.lang.String)
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
	public static class StringProperty extends AbstractProperty<String> {

		/**
		 * @param name
		 */
		public StringProperty(String name) {
			super(name);
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#getDefaultValue()
		 */
		@Override
		public String getDefaultValue() {
			return StringUtils.EMPTY;
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#fromValue(java.lang.Object)
		 */
		@Override
		public String fromValue(String v) {
			return v.toString();
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#toValue(java.lang.String)
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
	public static abstract class AbstractProperty<V> implements Property<V> {

		private final String name;

		/**
		 * @param name
		 */
		public AbstractProperty(String name) {
			this.name = Objects.requireNonNull(name);
		}

		/**
		 * @see org.fagu.fmv.core.project.Property#name()
		 */
		@Override
		public String name() {
			return name;
		}

	}

	// -------------------------------------------------------------------

	/**
	 *
	 */
	private Properties() {}

}
