package org.fagu.fmv.ffmpeg.utils;

import java.util.Optional;

import org.fagu.fmv.utils.time.Duration;


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
