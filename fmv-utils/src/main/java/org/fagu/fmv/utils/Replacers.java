package org.fagu.fmv.utils;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * @author f.agu
 */
public class Replacers implements Replacer {

	private static final Replacer UNRESOLVABLE_COPY = new Replacer() {

		/**
		 * @see org.fagu.fmv.utils.Replacer#init(org.fagu.fmv.utils.PlaceHolder)
		 */
		@Override
		public void init(PlaceHolder placeHolder) {
			placeHolder.circularKeyCopy();
		}

		/**
		 * @see org.fagu.fmv.utils.Replacer#replace(java.lang.String)
		 */
		@Override
		public String replace(String keyName) {
			return PlaceHolder.wrapKey(keyName);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "unresolvableCopy";
		}
	};

	private static final Replacer UNRESOLVABLE_IGNORE = ignore("");

	private final List<Replacer> replacerList;

	/**
	 *
	 */
	private Replacers() {
		replacerList = new ArrayList<>();
	}

	// ******************************************************

	/**
	 * @return
	 */
	public static Replacers chain() {
		return new Replacers();
	}

	/**
	 * @see org.fagu.fmv.utils.Replacer#init(org.fagu.fmv.utils.PlaceHolder)
	 */
	@Override
	public void init(PlaceHolder placeHolder) {
		replacerList.stream().forEach(r -> r.init(placeHolder));
	}

	/**
	 * @see org.fagu.fmv.utils.Replacer#replace(java.lang.String)
	 */
	@Override
	public String replace(String keyName) {
		return replacerList.stream()
				.map(r -> r.replace(keyName))
				.filter(Objects::nonNull)
				.findFirst()
				.orElseThrow(() -> new PlaceHolderException("Could not resolve placeholder '" + keyName + '\''));
	}

	/**
	 * @param replacer
	 * @return
	 */
	public Replacers of(Replacer replacer) {
		replacerList.add(Objects.requireNonNull(replacer));
		return this;
	}

	/**
	 * @param replacer
	 * @param replacerName
	 * @return
	 */
	public Replacers of(Replacer replacer, String replacerName) {
		Objects.requireNonNull(replacer);
		replacerList.add(new Replacer() {

			/**
			 * @see org.fagu.fmv.utils.Replacer#replace(java.lang.String)
			 */
			@Override
			public String replace(String keyName) {
				return replacer.replace(keyName);
			}

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return replacerName;
			}
		});
		return this;
	}

	/**
	 * @param replacerMap
	 * @return
	 */
	public Replacers map(Map<String, String> replacerMap) {
		return mapReplacer("map", replacerMap);
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public Replacers keyValue(String key, String value) {
		return mapReplacer("keyValue", Collections.singletonMap(key, value));
	}

	/**
	 * @return
	 */
	public Replacers ofEnv() {
		return mapReplacer("env", System.getenv());
	}

	/**
	 * @return
	 */
	public Replacers ofSystemProperties() {
		@SuppressWarnings({"rawtypes", "unchecked"})
		Map<String, String> map = (Map)System.getProperties();
		return mapReplacer("systemproperties", map);
	}

	/**
	 * @param namePredicate
	 * @param replacer
	 * @return
	 */
	public Replacers functionNamed(String functionName, Replacer replacer) {
		Objects.requireNonNull(functionName);
		return functionNamed(name -> Objects.equals(name, functionName), ":", replacer);
	}

	/**
	 * @param namePredicate
	 * @param separator
	 * @param replacer
	 * @return
	 */
	public Replacers functionNamed(Predicate<String> namePredicate, String separator, Replacer replacer) {
		Objects.requireNonNull(namePredicate);
		Objects.requireNonNull(separator);
		Objects.requireNonNull(replacer);
		return of(new Replacer() {

			/**
			 * @see org.fagu.fmv.utils.Replacer#replace(java.lang.String)
			 */
			@Override
			public String replace(String keyName) {
				int posSep = keyName.indexOf(separator);
				String name = posSep < 0 ? keyName : keyName.substring(0, posSep);
				if(namePredicate.test(name)) {
					String fctValue = posSep < 0 ? "" : keyName.substring(posSep + separator.length());
					return replacer.replace(fctValue);
				}
				return null;
			}

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "functionNamed(" + separator + ';' + replacer + ')';
			}
		});
	}

	/**
	 * @param findConsumer
	 * @return
	 */
	public Replacers eventFind(Consumer<String> findConsumer) {
		return of(new Replacer() {

			@Override
			public String replace(String keyName) {
				findConsumer.accept(keyName);
				return null;
			}

			@Override
			public String toString() {
				return "eventFind";
			}
		});
	}

	/**
	 * @return
	 */
	public Replacer unresolvableCopy() {
		return of(UNRESOLVABLE_COPY);
	}

	/**
	 * @return
	 */
	public Replacer unresolvableIgnored() {
		return of(UNRESOLVABLE_IGNORE);
	}

	/**
	 * @return
	 */
	public Replacer unresolvableIgnored(String replaceBy) {
		return of(ignore(replaceBy));
	}

	/**
	 * @return
	 */
	public Replacer unresolvableThrow() {
		return unresolvableThrow(k -> new PlaceHolderException("Could not resolve placeholder '" + k + '\''));
	}

	/**
	 * @param supplierThrowable
	 * @return
	 */
	public Replacer unresolvableThrow(Function<String, ? extends RuntimeException> supplierThrowable) {
		return of(new Replacer() {

			/**
			 * @see org.fagu.fmv.utils.Replacer#replace(java.lang.String)
			 */
			@Override
			public String replace(String keyName) {
				throw supplierThrowable.apply(keyName);
			}

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "unresolvableThrow";
			}
		});
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(replacerList.isEmpty()) {
			return "chain empty";
		}
		if(replacerList.size() == 1) {
			return replacerList.get(0).toString();
		}
		return replacerList.toString();
	}

	// ****************************************************

	/**
	 * @param by
	 * @return
	 */
	private static Replacer ignore(String by) {
		return new Replacer() {

			/**
			 * @see org.fagu.fmv.utils.Replacer#init(org.fagu.fmv.utils.PlaceHolder)
			 */
			@Override
			public void init(PlaceHolder placeHolder) {
				placeHolder.circular(s -> by);
			}

			/**
			 * @see org.fagu.fmv.utils.Replacer#replace(java.lang.String)
			 */
			@Override
			public String replace(String keyName) {
				return by;
			}

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "unresolvableIgnore(" + by + ')';
			}
		};
	}

	/**
	 * @param name
	 * @param replacerMap
	 * @return
	 */
	private Replacers mapReplacer(String name, Map<String, String> replacerMap) {
		Objects.requireNonNull(name);
		return of(new Replacer() {

			/**
			 * @see org.fagu.fmv.utils.Replacer#replace(java.lang.String)
			 */
			@Override
			public String replace(String keyName) {
				return replacerMap.get(keyName);
			}

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				StringBuilder buf = new StringBuilder();
				buf.append(name).append(replacerMap.toString());
				return buf.toString();
			}
		});
	}

}
