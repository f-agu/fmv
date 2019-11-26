package org.fagu.fmv.ffmpeg.metadatas;

import java.util.Map;

import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class DataStream extends Stream {

	public DataStream(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	@Override
	public Type type() {
		return Type.DATA;
	}
}
