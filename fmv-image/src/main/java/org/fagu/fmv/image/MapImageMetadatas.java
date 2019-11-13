package org.fagu.fmv.image;

import java.util.NavigableMap;

import org.fagu.fmv.media.NavigableMapMetadatasContainer;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 nov. 2019 10:55:09
 */
public abstract class MapImageMetadatas extends NavigableMapMetadatasContainer implements ImageMetadatas {

	private final long createTime;

	protected MapImageMetadatas(NavigableMap<String, Object> metadatas) {
		super(metadatas);
		createTime = System.currentTimeMillis();
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

}
