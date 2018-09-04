package org.fagu.fmv.soft.mediainfo;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.OptionalDouble;
import java.util.TreeMap;


/**
 * @author f.agu
 * @created 7 avr. 2018 15:04:53
 */
public class EncodingSettings implements ParsableType {

	private final NavigableMap<String, String> settingsMap;

	EncodingSettings(NavigableMap<String, String> settingsMap) {
		this.settingsMap = Collections.unmodifiableNavigableMap(new TreeMap<>(settingsMap));
	}

	@Override
	public String getString(String key) {
		return settingsMap.get(key);
	}

	public OptionalDouble getCRF() {
		return getDouble("crf");
	}
}
