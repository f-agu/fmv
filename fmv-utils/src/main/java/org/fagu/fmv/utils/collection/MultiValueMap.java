package org.fagu.fmv.utils.collection;

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


import java.util.Collection;
import java.util.Map;


/**
 * @author f.agu
 */
public interface MultiValueMap<K, C extends Collection<V>, V> extends Map<K, C> {

	/**
	 * @param key
	 */
	C addEmpty(K key);

	/**
	 * @param key
	 * @param value
	 */
	boolean add(K key, V value);

	/**
	 * @param key
	 * @param values
	 */
	boolean addAll(K key, Collection<V> values);

	/**
	 * @param key
	 * @return
	 */
	int size(K key);

	/**
	 * @param key
	 * @return
	 */
	int sizeValues();

	/**
	 * @param key
	 * @return
	 */
	V getFirst(K key);

}
