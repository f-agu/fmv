package org.fagu.fmv.ffmpeg.utils;

import java.util.Optional;


/**
 * @author fagu
 */
@FunctionalInterface
public interface Durable {

	/**
	 * @return
	 */
	Optional<Duration> getDuration();
}
