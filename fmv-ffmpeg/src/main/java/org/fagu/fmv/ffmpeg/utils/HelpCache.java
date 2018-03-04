package org.fagu.fmv.ffmpeg.utils;

/*
 * #%L
 * fmv-ffmpeg
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author f.agu
 */
public class HelpCache<E, H extends Help> {

	// -----------------------------------------

	/**
	 * @author f.agu
	 */
	private static class Both<E, H> {

		private E e;

		private List<H> hList = new ArrayList<>();

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return e + ";" + hList;
		}
	}

	// -----------------------------------------

	private final Map<String, Both<E, H>> cacheMap = new TreeMap<>();

	private List<E> entityList;

	private boolean availableLoaded;

	private final Runnable runnable;

	private final Function<String, E> eFactory;

	/**
	 * @param runnable
	 * @param eFactory
	 */
	public HelpCache(Runnable runnable, Function<String, E> eFactory) {
		this.runnable = runnable;
		this.eFactory = eFactory;
	}

	/**
	 * @return the consumer
	 */
	public Consumer<H> consumer() {
		return h -> add(h.getName(), null, h);
	}

	/**
	 * @param name
	 * @param e
	 * @param h
	 */
	public void add(String name, E e, H h) {
		if(name == null) {
			return;
		}
		Both<E, H> both = cacheMap.computeIfAbsent(name, k -> new Both<>());
		if(e != null && both.e == null) {
			both.e = e;
		}
		if(h != null) {
			both.hList.add(h);
			if(both.e == null) {
				both.e = eFactory.apply(name);
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public List<H> cache(String name) {
		load();
		Both<E, H> both = cacheMap.get(name);
		if(both == null || both.hList.isEmpty()) {
			return Collections.emptyList();
		}
		return both.hList;
	}

	/**
	 * @param name
	 * @return
	 */
	public boolean exists(String name) {
		return cache(name).stream().findFirst().isPresent();
	}

	/**
	 * @return
	 */
	public Set<String> availableNames() {
		load();
		return cacheMap.keySet();
	}

	/**
	 * @return
	 */
	public List<E> available() {
		if(entityList == null) {
			load();
			entityList = cacheMap.values().stream().map(b -> b.e).filter(Objects::nonNull).collect(Collectors.toList());
		}
		return entityList;
	}

	/**
	 * @return
	 */
	public E byName(String name) {
		load();
		Both<E, H> both = cacheMap.get(name);
		return both == null ? null : both.e;
	}

	// **************************************

	/**
	 * @return
	 */
	public void load() {
		if( ! availableLoaded) {
			availableLoaded = true;
			runnable.run();
		}
	}

}
