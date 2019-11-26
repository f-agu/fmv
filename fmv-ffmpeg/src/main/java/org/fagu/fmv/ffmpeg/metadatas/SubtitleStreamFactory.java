package org.fagu.fmv.ffmpeg.metadatas;

import java.util.Map;


/**
 * @author f.agu
 */
public class SubtitleStreamFactory implements StreamFactory {

	@Override
	public Stream create(MovieMetadatas movieMetadatas, Map<String, Object> map) {
		return new SubtitleStream(movieMetadatas, map);
	}

}
