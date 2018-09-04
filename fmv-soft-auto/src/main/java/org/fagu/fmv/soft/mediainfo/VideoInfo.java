package org.fagu.fmv.soft.mediainfo;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 * @created 7 avr. 2018 14:16:16
 */
public class VideoInfo extends InfoBase {

	private EncodingSettings encodingSettings;

	public VideoInfo(int indexByType, Map<String, String> infoMap) {
		super(indexByType, infoMap);
	}

	@Override
	public InfoType getType() {
		return InfoType.VIDEO;
	}

	public EncodingSettings getEncodingSettings() {
		if(encodingSettings == null) {
			String value = getString("Encoding settings");
			NavigableMap<String, String> settingMap;
			if(StringUtils.isEmpty(value)) {
				settingMap = Collections.emptyNavigableMap();
			} else {
				settingMap = new TreeMap<>();
				for(String split : value.split(" / ")) {
					int pos = split.indexOf('=');
					settingMap.put(split.substring(0, pos), split.substring(pos + 1));
				}
			}
			encodingSettings = new EncodingSettings(settingMap);
		}
		return encodingSettings;
	}
}
