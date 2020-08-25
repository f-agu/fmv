package org.fagu.fmv.soft.mediainfo;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.fagu.fmv.media.MetadatasContainer;


/**
 * @author f.agu
 * @created 7 avr. 2018 14:09:58
 */
public abstract class InfoBase implements MetadatasContainer { // ParsableType

	private final int indexByType;

	private final NavigableMap<String, Object> infoMap;

	public InfoBase(int indexByType, Map<String, Object> infoMap) {
		this.indexByType = indexByType;
		this.infoMap = Collections.unmodifiableNavigableMap(new TreeMap<>(infoMap));
	}

	public abstract InfoType getType();

	public int getIndexByType() {
		return indexByType;
	}

	// @Override
	// public String getString(String key) {
	// return infoMap.get(key);
	// }
	//
	// public NavigableMap<String, String> getDataMap() {
	// return infoMap;
	// }

	@Override
	public Map<String, Object> getData() {
		return infoMap;
	}

}
