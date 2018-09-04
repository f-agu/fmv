package org.fagu.fmv.soft.mediainfo;

import java.util.Map;


/**
 * @author f.agu
 * @created 7 avr. 2018 14:16:16
 */
public class MenuInfo extends InfoBase {

	public MenuInfo(int indexByType, Map<String, String> infoMap) {
		super(indexByType, infoMap);
	}

	@Override
	public InfoType getType() {
		return InfoType.MENU;
	}

}
