package org.fagu.fmv.media;

/*-
 * #%L
 * fmv-media
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

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;


/**
 * @author Oodrive
 * @author f.agu
 * @created 13 nov. 2019 11:21:52
 */
public class NavigableMapMetadatasContainer implements MetadatasContainer {

	private final NavigableMap<String, Object> metadatas;

	public NavigableMapMetadatasContainer(Map<String, Object> metadatas) {
		TreeMap<String, Object> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		map.putAll(metadatas);
		this.metadatas = Collections.unmodifiableNavigableMap(map);
	}

	@Override
	public Map<String, Object> getData() {
		return metadatas;
	}

	@Override
	public String toString() {
		return metadatas.toString();
	}

}
