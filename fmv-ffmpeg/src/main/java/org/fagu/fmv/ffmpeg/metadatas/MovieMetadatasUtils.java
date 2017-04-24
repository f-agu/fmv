package org.fagu.fmv.ffmpeg.metadatas;

import java.util.Optional;

import org.fagu.fmv.ffmpeg.utils.Duration;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 avr. 2017 13:58:20
 */
public class MovieMetadatasUtils {

	/**
	 * 
	 */
	private MovieMetadatasUtils() {}

	/**
	 * @param movieMetadatas
	 * @return
	 */
	public static Optional<Duration> getDuration(MovieMetadatas movieMetadatas) {
		VideoStream videoStream = movieMetadatas.getVideoStream();
		if(videoStream != null) {
			return Optional.ofNullable(videoStream.duration());
		}
		AudioStream audioStream = movieMetadatas.getAudioStream();
		if(audioStream != null) {
			return Optional.ofNullable(audioStream.duration());
		}
		return Optional.empty();
	}

}
