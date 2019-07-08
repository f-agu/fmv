package org.fagu.fmv.soft.mediainfo;

import java.util.Map;


/**
 * @author Oodrive
 * @author f.agu
 * @created 3 juil. 2019 11:11:26
 */
public class OtherInfo extends InfoBase {

	public OtherInfo(int indexByType, Map<String, String> infoMap) {
		super(indexByType, infoMap);
	}

	@Override
	public InfoType getType() {
		return InfoType.OTHER;
	}

}
