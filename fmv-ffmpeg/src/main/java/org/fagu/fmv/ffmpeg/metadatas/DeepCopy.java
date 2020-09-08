package org.fagu.fmv.ffmpeg.metadatas;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;


/**
 * @author Oodrive
 * @author f.agu
 * @created 26 nov. 2019 10:13:57
 */
class DeepCopy {

	private DeepCopy() {}

	static NavigableMap<String, Object> copy(Map<String, Object> map) {
		NavigableMap<String, Object> nvmap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for(Entry<String, Object> entry : map.entrySet()) {
			callOrSet(entry.getValue(), o -> nvmap.put(entry.getKey(), o));
		}
		return Collections.unmodifiableNavigableMap(nvmap);
	}

	static Collection<Object> copy(Collection<Object> collection) {
		if(collection instanceof Set) {
			Set<Object> set = new TreeSet<>();
			for(Object obj : collection) {
				callOrSet(obj, set::add);
			}
			return Collections.unmodifiableSet(set);
		}
		List<Object> list = new ArrayList<>();
		for(Object obj : list) {
			callOrSet(obj, list::add);
		}
		return Collections.unmodifiableList(list);
	}

	// *************************************************

	@SuppressWarnings("unchecked")
	private static void callOrSet(Object obj, Consumer<Object> appender) {
		if(obj instanceof Map) {
			appender.accept(copy((Map<String, Object>)obj));
		} else if(obj instanceof Collection) {
			appender.accept(copy((Collection<Object>)obj));
		} else {
			appender.accept(obj);
		}
	}

}
