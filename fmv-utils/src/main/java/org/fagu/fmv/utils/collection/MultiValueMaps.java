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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;


/**
 * @author f.agu
 */
public class MultiValueMaps {

	protected MultiValueMaps() {}

	// ****

	public static <K, V> MapList<K, V> hashMapArrayList() {
		return new MyMapList<>(new HashMap<K, List<V>>(), ArrayList::new);
	}

	public static <K, V> MapSet<K, V> hashMapHashSet() {
		return new MyMapSet<>(new HashMap<K, Set<V>>(), HashSet::new);
	}

	public static <K, V> MapSortedSet<K, V> hashMapTreeSet() {
		return new MyMapSortedSet<>(new HashMap<K, SortedSet<V>>(), TreeSet::new);
	}

	// ****

	public static <K, V> MapList<K, V> arrayList(Map<K, List<V>> map) {
		return new MyMapList<>(map, ArrayList::new);
	}

	public static <K, V> MapSet<K, V> hashSet(Map<K, Set<V>> map) {
		return new MyMapSet<>(map, HashSet::new);
	}

	// ****

	public static <K, V> MapList<K, V> list(Map<K, List<V>> map, Supplier<List<V>> supplier) {
		return new MyMapList<>(map, supplier);
	}

	public static <K, V> MapSet<K, V> set(Map<K, Set<V>> map, Supplier<Set<V>> supplier) {
		return new MyMapSet<>(map, supplier);
	}

	public static <K, V> MapSortedSet<K, V> sortedSet(Map<K, SortedSet<V>> map, Supplier<SortedSet<V>> supplier) {
		return new MyMapSortedSet<>(map, supplier);
	}

	// ------------------------------------------

	/**
	 * @author f.agu
	 * 
	 * @param <K>
	 * @param <V>
	 */
	protected static class MyMapList<K, V> extends MyMultiValueMap<K, List<V>, V> implements MapList<K, V>, Serializable {

		private static final long serialVersionUID = - 605564461741729218L;

		public MyMapList(Map<K, List<V>> map, Supplier<List<V>> supplier) {
			super(map, supplier);
		}
	}

	// ------------------------------------------

	/**
	 * @author f.agu
	 * 
	 * @param <K>
	 * @param <V>
	 */
	protected static class MyMapSet<K, V> extends MyMultiValueMap<K, Set<V>, V> implements MapSet<K, V>, Serializable {

		private static final long serialVersionUID = 4020967906109471472L;

		public MyMapSet(Map<K, Set<V>> map, Supplier<Set<V>> supplier) {
			super(map, supplier);
		}
	}

	// ------------------------------------------

	/**
	 * @author f.agu
	 * 
	 * @param <K>
	 * @param <V>
	 */
	protected static class MyMapSortedSet<K, V> extends MyMultiValueMap<K, SortedSet<V>, V> implements MapSortedSet<K, V>, Serializable {

		private static final long serialVersionUID = 5347119512400623956L;

		public MyMapSortedSet(Map<K, SortedSet<V>> map, Supplier<SortedSet<V>> supplier) {
			super(map, supplier);
		}
	}

	// ------------------------------------------

	/**
	 * @author f.agu
	 * 
	 * @param <K>
	 * @param <C>
	 * @param <V>
	 */
	protected static class MyMultiValueMap<K, C extends Collection<V>, V> implements MultiValueMap<K, C, V>, Serializable {

		private static final long serialVersionUID = - 8243964874408486073L;

		protected final Map<K, C> map;

		protected final Supplier<C> supplier;

		protected MyMultiValueMap(Map<K, C> map, Supplier<C> supplier) {
			this.map = map;
			this.supplier = supplier;
		}

		@Override
		public C addEmpty(K key) {
			return getOrCreateCollection(key);
		}

		@Override
		public boolean add(K key, V value) {
			C c = getOrCreateCollection(key);
			return c.add(value);
		}

		@Override
		public boolean addAll(K key, Collection<V> values) {
			C c = getOrCreateCollection(key);
			return c.addAll(values);
		}

		@Override
		public int size(K key) {
			C c = map.get(key);
			return c != null ? c.size() : 0;
		}

		@Override
		public int sizeValues() {
			int count = 0;
			for(C c : map.values()) {
				count += c.size();
			}
			return count;
		}

		@Override
		public V getFirst(K key) {
			C c = map.get(key);
			return c != null && ! c.isEmpty() ? c.iterator().next() : null;
		}

		@Override
		public int size() {
			return map.size();
		}

		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return map.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			for(C c : map.values()) {
				if(c.contains(value)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public C get(Object key) {
			return map.get(key);
		}

		@Override
		public C put(K key, C value) {
			return map.put(key, value);
		}

		@Override
		public C remove(Object key) {
			return map.remove(key);
		}

		@Override
		public void putAll(Map<? extends K, ? extends C> m) {
			for(Entry<? extends K, ? extends C> entry : m.entrySet()) {
				K k = entry.getKey();
				C c = getOrCreateCollection(k);
				c.addAll(entry.getValue());
			}
		}

		@Override
		public void clear() {
			map.clear();
		}

		@Override
		public Set<K> keySet() {
			return map.keySet();
		}

		@Override
		public Collection<C> values() {
			return map.values();
		}

		@Override
		public Set<java.util.Map.Entry<K, C>> entrySet() {
			return map.entrySet();
		}

		@Override
		public int hashCode() {
			return map.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return map.equals(obj);
		}

		@Override
		public String toString() {
			return map.toString();
		}

		// ***************************************

		private C getOrCreateCollection(K k) {
			C c = map.get(k);
			if(c == null) {
				c = supplier.get();
				// c = collectionFactory.create();
				map.put(k, c);
			}
			return c;
		}

	}

}
