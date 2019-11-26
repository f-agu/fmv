package org.fagu.fmv.ffmpeg.metadatas;

import java.util.Map;

import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class SubtitleStream extends Stream {

	public SubtitleStream(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		super(movieMetadatas, map);
	}

	@Override
	public Type type() {
		return Type.SUBTITLE;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		String language = language().orElse(null);
		return buf.append("SubtitleStream[").append(language != null ? language : '?').append(']')
				.toString();
	}
}
