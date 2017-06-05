package org.fagu.fmv.soft.mplayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Oodrive
 * @author f.agu
 * @created 5 juin 2017 13:28:15
 */
public abstract class Stream {

	private final int num;

	private final Map<String, String> properties;

	public Stream(int num, Map<String, String> properties) {
		this.num = num;
		this.properties = Collections.unmodifiableMap(new HashMap<>(properties));
	}

	public int getNum() {
		return num;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public String getLanguage() {
		return properties.get("language");
	}
}
