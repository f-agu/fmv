package org.fagu.fmv.soft.mediainfo;

import java.util.Map;


/**
 * @author f.agu
 * @created 3 juil. 2019 11:11:26
 */
public class OtherInfo extends InfoBase {

	public OtherInfo(int indexByType, Map<String, Object> infoMap) {
		super(indexByType, infoMap);
	}

	@Override
	public InfoType getType() {
		return InfoType.OTHER;
	}

}
