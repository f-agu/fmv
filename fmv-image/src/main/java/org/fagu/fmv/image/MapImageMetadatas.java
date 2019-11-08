package org.fagu.fmv.image;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 nov. 2019 10:55:09
 */
public abstract class MapImageMetadatas implements ImageMetadatas {

	private final long createTime;

	protected final NavigableMap<String, String> metadatas;

	protected MapImageMetadatas(TreeMap<String, String> metadatas) {
		this.metadatas = Collections.unmodifiableNavigableMap(new TreeMap<>(metadatas));
		createTime = System.currentTimeMillis();
	}

	@Override
	public NavigableMap<String, String> getMetadatas() {
		return metadatas;
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

	@Override
	public String toString() {
		return metadatas.toString();
	}

}
