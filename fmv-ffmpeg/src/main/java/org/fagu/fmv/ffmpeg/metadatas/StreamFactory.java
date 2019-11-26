package org.fagu.fmv.ffmpeg.metadatas;

import java.util.Map;


/**
 * @author f.agu
 */
@FunctionalInterface
public interface StreamFactory {

	Stream create(MovieMetadatas movieMetadatas, Map<String, Object> map);
}
