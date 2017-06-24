package org.fagu.fmv.ffmpeg.progressbar;

import java.util.Objects;

import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.ffmpeg.progressbar.ByDuration.ByDurationBuilder;
import org.fagu.fmv.ffmpeg.progressbar.ByFrame.ByFrameBuilder;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author fagu
 */
public class FFMpegProgressBar {

	private final Progress progress;

	/**
	 * @param progress
	 */
	private FFMpegProgressBar(Progress progress) {
		this.progress = Objects.requireNonNull(progress);
	}

	/**
	 * @param progress
	 * @return
	 */
	public static FFMpegProgressBar with(Progress progress) {
		return new FFMpegProgressBar(progress);
	}

	/**
	 * @param duration
	 * @return
	 */
	public ByDurationBuilder byDuration(Duration duration) {
		return new ByDurationBuilder(progress, duration);
	}

	/**
	 * @param numberOfFrames
	 * @return
	 */
	public ByFrameBuilder byFrame(int numberOfFrames) {
		return new ByFrameBuilder(progress, numberOfFrames);
	}

}
