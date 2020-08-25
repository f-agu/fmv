package org.fagu.fmv.soft.mediainfo;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;


/**
 * @author f.agu
 * @created 7 avr. 2018 14:16:16
 */
public class VideoInfo extends InfoBase {

	private EncodingSettings encodingSettings;

	public VideoInfo(int indexByType, Map<String, Object> infoMap) {
		super(indexByType, infoMap);
	}

	@Override
	public InfoType getType() {
		return InfoType.VIDEO;
	}

	public Optional<Integer> getWidth() {
		return getFirstInteger("Width");
	}

	public Optional<Integer> getHeight() {
		return getFirstInteger("Height");
	}

	public EncodingSettings getEncodingSettings() {
		if(encodingSettings == null) {
			encodingSettings = new EncodingSettings(
					getFirstString("Encoding settings")
							.map(v -> {
								NavigableMap<String, String> settingMap = new TreeMap<>();
								for(String split : v.split(" / ")) {
									int pos = split.indexOf('=');
									settingMap.put(split.substring(0, pos), split.substring(pos + 1));
								}
								return settingMap;
							})
							.orElseGet(Collections::emptyNavigableMap));
		}
		return encodingSettings;
	}
}
