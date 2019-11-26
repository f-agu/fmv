package org.fagu.fmv.ffmpeg.metadatas;

import java.util.Map;

import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class UnknownStream extends Stream {

	public UnknownStream(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	@Override
	public Type type() {
		return Type.UNKNOWN;
	}
}
