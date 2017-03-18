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

	/**
	 * 
	 */
	protected MultiValueMaps() {}

	// ****

	/**
	 * @return
	 */
	public static <K, V> MapList<K, V> hashMapArrayList() {
		return new MyMapList<>(new HashMap<K, List<V>>(), ArrayList::new);
	}

	/**
	 * @return
	 */
	public static <K, V> MapSet<K, V> hashMapHashSet() {
		return new MyMapSet<>(new HashMap<K, Set<V>>(), HashSet::new);
	}

	/**
	 * @return
	 */
	public static <K, V> MapSortedSet<K, V> hashMapTreeSet() {
		return new MyMapSortedSet<>(new HashMap<K, SortedSet<V>>(), TreeSet::new);
	}

	/**
	 * @return
	 */
	public static <K1, K2, V> MapMap<K1, K2, V> hashMapHashMap() {
		return new MyMapMap<>(new HashMap<K1, Map<K2, V>>(), HashMap::new);
	}

	// ****

	/**
	 * @param map
	 * @return
	 */
	public static <K, V> MapList<K, V> arrayList(Map<K, List<V>> map) {
		return new MyMapList<>(map, ArrayList::new);
	}

	/**
	 * @param map
	 * @return
	 */
	public static <K, V> MapSet<K, V> hashSet(Map<K, Set<V>> map) {
		return new MyMapSet<>(map, HashSet::new);
	}

	/**
	 * @param map
	 * @return
	 */
	public static <K1, K2, V> MapMap<K1, K2, V> hashMap(Map<K1, Map<K2, V>> map) {
		return new MyMapMap<>(map, HashMap::new);
	}

	// ****

	/**
	 * @param map
	 * @param listFactory
	 * @return
	 */
	public static <K, V> MapList<K, V> list(Map<K, List<V>> map, Supplier<List<V>> supplier) {
		return new MyMapList<>(map, supplier);
	}

	/**
	 * @param map
	 * @param setFactory
	 * @return
	 */
	public static <K, V> MapSet<K, V> set(Map<K, Set<V>> map, Supplier<Set<V>> supplier) {
		return new MyMapSet<>(map, supplier);
	}

	/**
	 * @param map
	 * @param sortedSetFactory
	 * @return
	 */
	public static <K, V> MapSortedSet<K, V> sortedSet(Map<K, SortedSet<V>> map, Supplier<SortedSet<V>> supplier) {
		return new MyMapSortedSet<>(map, supplier);
	}

	/**
	 * @param map
	 * @param mapFactory
	 * @return
	 */
	public static <K1, K2, V> MapMap<K1, K2, V> map(Map<K1, Map<K2, V>> map, Supplier<Map<K2, V>> mapFactory) {
		return new MyMapMap<>(map, mapFactory);
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

		/**
		 * @param map
		 * @param supplier
		 */
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

		/**
		 * @param map
		 * @param supplier
		 */
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

		/**
		 * @param map
		 * @param supplier
		 */
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

		/**
		 * @param map
		 * @param supplier
		 */
		protected MyMultiValueMap(Map<K, C> map, Supplier<C> supplier) {
			this.map = map;
			this.supplier = supplier;
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MultiValueMap#addEmpty(java.lang.Object)
		 */
		@Override
		public C addEmpty(K key) {
			return getOrCreateCollection(key);
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MultiValueMap#add(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean add(K key, V value) {
			C c = getOrCreateCollection(key);
			return c.add(value);
		}

		/**
		 * @param key
		 * @param values
		 */
		@Override
		public boolean addAll(K key, Collection<V> values) {
			C c = getOrCreateCollection(key);
			return c.addAll(values);
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MultiValueMap#size(java.lang.Object)
		 */
		@Override
		public int size(K key) {
			C c = map.get(key);
			return c != null ? c.size() : 0;
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MultiValueMap#sizeValues()
		 */
		@Override
		public int sizeValues() {
			int count = 0;
			for(C c : map.values()) {
				count += c.size();
			}
			return count;
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MultiValueMap#getFirst(java.lang.Object)
		 */
		@Override
		public V getFirst(K key) {
			C c = map.get(key);
			return c != null && ! c.isEmpty() ? c.iterator().next() : null;
		}

		/**
		 * @see java.util.Map#size()
		 */
		@Override
		public int size() {
			return map.size();
		}

		/**
		 * @see java.util.Map#isEmpty()
		 */
		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}

		/**
		 * @see java.util.Map#containsKey(java.lang.Object)
		 */
		@Override
		public boolean containsKey(Object key) {
			return map.containsKey(key);
		}

		/**
		 * @see java.util.Map#containsValue(java.lang.Object)
		 */
		@Override
		public boolean containsValue(Object value) {
			for(C c : map.values()) {
				if(c.contains(value)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * @see java.util.Map#get(java.lang.Object)
		 */
		@Override
		public C get(Object key) {
			return map.get(key);
		}

		/**
		 * @param key
		 * @param value
		 * @return
		 */
		@Override
		public C put(K key, C value) {
			return map.put(key, value);
		}

		/**
		 * @see java.util.Map#remove(java.lang.Object)
		 */
		@Override
		public C remove(Object key) {
			return map.remove(key);
		}

		/**
		 * @see java.util.Map#putAll(java.util.Map)
		 */
		@Override
		public void putAll(Map<? extends K, ? extends C> m) {
			for(Entry<? extends K, ? extends C> entry : m.entrySet()) {
				K k = entry.getKey();
				C c = getOrCreateCollection(k);
				c.addAll(entry.getValue());
			}
		}

		/**
		 * @see java.util.Map#clear()
		 */
		@Override
		public void clear() {
			map.clear();
		}

		/**
		 * @see java.util.Map#keySet()
		 */
		@Override
		public Set<K> keySet() {
			return map.keySet();
		}

		/**
		 * @see java.util.Map#values()
		 */
		@Override
		public Collection<C> values() {
			return map.values();
		}

		/**
		 * @see java.util.Map#entrySet()
		 */
		@Override
		public Set<java.util.Map.Entry<K, C>> entrySet() {
			return map.entrySet();
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return map.hashCode();
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return map.equals(obj);
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return map.toString();
		}

		// ***************************************

		/**
		 * @param k
		 * @return
		 */
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

	// ------------------------------------------

	/**
	 * @author f.agu
	 * 
	 * @param <K>
	 * @param <C>
	 * @param <V>
	 */
	protected static class MyMapMap<K1, K2, V> implements MapMap<K1, K2, V>, Serializable {

		private static final long serialVersionUID = - 5354764357786496133L;

		protected final Map<K1, Map<K2, V>> map;

		protected final Supplier<Map<K2, V>> mapSupplier;

		/**
		 * @param map
		 * @param collectionFactory
		 */
		protected MyMapMap(Map<K1, Map<K2, V>> map, Supplier<Map<K2, V>> mapSupplier) {
			this.map = map;
			this.mapSupplier = mapSupplier;
		}

		/**
		 * @see java.util.Map#size()
		 */
		@Override
		public int size() {
			return map.size();
		}

		/**
		 * @see java.util.Map#isEmpty()
		 */
		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}

		/**
		 * @see java.util.Map#containsKey(java.lang.Object)
		 */
		@Override
		public boolean containsKey(Object key) {
			return map.containsKey(key);
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MapMap#containsKeys(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean containsKeys(K1 k1, K2 k2) {
			Map<K2, V> map2 = map.get(k1);
			return map2 == null ? false : map2.containsKey(k2);
		}

		/**
		 * @see java.util.Map#containsValue(java.lang.Object)
		 */
		@Override
		public boolean containsValue(Object value) {
			return map.containsValue(value);
		}

		/**
		 * @see java.util.Map#get(java.lang.Object)
		 */
		@Override
		public Map<K2, V> get(Object key) {
			return map.get(key);
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MapMap#get(java.lang.Object, java.lang.Object)
		 */
		@Override
		public V get(K1 k1, K2 k2) {
			Map<K2, V> map2 = map.get(k1);
			return map2 != null ? map2.get(k2) : null;
		}

		/**
		 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Map<K2, V> put(K1 k1, Map<K2, V> value) {
			Map<K2, V> map2 = getOrCreateMap(k1);
			map2.putAll(value);
			return null; // TODO
		}

		/**
		 * @see java.util.Map#remove(java.lang.Object)
		 */
		@Override
		public Map<K2, V> remove(Object key) {
			return map.remove(key);
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MapMap#removeFor(java.lang.Object, java.lang.Object)
		 */
		@Override
		public V removeFor(K1 k1, K2 k2) {
			Map<K2, V> map2 = map.get(k1);
			return map2 == null ? null : map2.remove(k2);
		}

		/**
		 * @see java.util.Map#putAll(java.util.Map)
		 */
		@Override
		public void putAll(Map<? extends K1, ? extends Map<K2, V>> m) {
			for(Entry<? extends K1, ? extends Map<K2, V>> entry : m.entrySet()) {
				K1 k1 = entry.getKey();
				Map<K2, V> map2 = getOrCreateMap(k1);
				map2.putAll(entry.getValue());
			}
		}

		/**
		 * @see java.util.Map#clear()
		 */
		@Override
		public void clear() {
			map.clear();
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MapMap#clear(java.lang.Object)
		 */
		@Override
		public void clear(K1 k1) {
			Map<K2, V> map2 = map.get(k1);
			if(map2 != null) {
				map2.clear();
			}
		}

		/**
		 * @see java.util.Map#keySet()
		 */
		@Override
		public Set<K1> keySet() {
			return map.keySet();
		}

		/**
		 * @see java.util.Map#values()
		 */
		@Override
		public Collection<Map<K2, V>> values() {
			return map.values();
		}

		/**
		 * @see java.util.Map#entrySet()
		 */
		@Override
		public Set<java.util.Map.Entry<K1, Map<K2, V>>> entrySet() {
			return map.entrySet();
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MapMap#add(java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		@Override
		public V add(K1 key1, K2 key2, V value) {
			Map<K2, V> map2 = getOrCreateMap(key1);
			return map2.put(key2, value);
		}

		/**
		 * @see org.fagu.fmv.utils.collection.MapMap#addEmpty(java.lang.Object)
		 */
		@Override
		public Map<K2, V> addEmpty(K1 key1) {
			return getOrCreateMap(key1);
		}

		/**
		 * @return
		 */
		@Override
		public int hashCode() {
			return map.hashCode();
		}

		/**
		 * @param obj
		 * @return
		 */
		@Override
		public boolean equals(Object obj) {
			return map.equals(obj);
		}

		/**
		 * @return
		 */
		@Override
		public String toString() {
			return map.toString();
		}

		// ***************************************

		/**
		 * @param k1
		 * @return
		 */
		private Map<K2, V> getOrCreateMap(K1 k1) {
			Map<K2, V> map2 = map.get(k1);
			if(map2 == null) {
				map2 = mapSupplier.get();
				map.put(k1, map2);
			}
			return map2;
		}

	}

}
