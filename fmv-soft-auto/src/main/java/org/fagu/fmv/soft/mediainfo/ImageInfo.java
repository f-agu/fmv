package org.fagu.fmv.soft.mediainfo;

import java.util.Map;
import java.util.Optional;


/**
 * @author f.agu
 * @created 3 juil. 2019 11:10:29
 */
public class ImageInfo extends InfoBase {

	public ImageInfo(int indexByType, Map<String, Object> infoMap) {
		super(indexByType, infoMap);
	}

	@Override
	public InfoType getType() {
		return InfoType.IMAGE;
	}

	public Optional<Integer> getWidth() {
		return getFirstInteger("Width");
	}

	public Optional<Integer> getHeight() {
		return getFirstInteger("Height");
	}

}
