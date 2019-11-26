package org.fagu.fmv.ffmpeg.metadatas;

import java.util.Map;

import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class AttachmentsStream extends Stream {

	public AttachmentsStream(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	@Override
	public Type type() {
		return Type.ATTACHEMENTS;
	}
}
