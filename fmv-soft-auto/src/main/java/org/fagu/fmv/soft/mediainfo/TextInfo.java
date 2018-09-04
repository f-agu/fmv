package org.fagu.fmv.soft.mediainfo;

import java.util.Map;


/**
 * @author f.agu
 * @created 7 avr. 2018 14:16:16
 */
public class TextInfo extends InfoBase {

	public TextInfo(int indexByType, Map<String, String> infoMap) {
		super(indexByType, infoMap);
	}

	@Override
	public InfoType getType() {
		return InfoType.TEXT;
	}

}
