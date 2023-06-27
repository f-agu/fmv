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

import java.util.Map;


/**
 * Map<K1, Map<K2, V>>
 * 
 * @author f.agu
 */
public interface MapMap<K1, K2, V> extends Map<K1, Map<K2, V>> {

	boolean containsKeys(K1 k1, K2 k2);

	Map<K2, V> addEmpty(K1 key1);

	V add(K1 key1, K2 key2, V value);

	V get(K1 k1, K2 k2);

	V removeFor(K1 k1, K2 k2);

	void clear(K1 k1);
}
