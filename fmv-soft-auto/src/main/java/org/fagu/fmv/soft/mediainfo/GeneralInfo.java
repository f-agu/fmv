package org.fagu.fmv.soft.mediainfo;

import java.util.Map;


/**
 * @author Utilisateur
 * @created 7 avr. 2018 14:16:16
 */
public class GeneralInfo extends InfoBase {

	public GeneralInfo(int indexByType, Map<String, String> infoMap) {
		super(indexByType, infoMap);
	}

	@Override
	public InfoType getType() {
		return InfoType.GENERAL;
	}

}