package org.fagu.fmv.media;

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

	public NavigableMapMetadatasContainer(NavigableMap<String, Object> metadatas) {
		this.metadatas = Collections.unmodifiableNavigableMap(new TreeMap<>(metadatas));
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
