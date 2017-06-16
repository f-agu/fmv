package org.fagu.fmv.mymedia.utils;

import java.io.Closeable;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.mymedia.utils.TextProgressBar.Chars;
import org.fagu.fmv.mymedia.utils.TextProgressBar.TextProgressBarBuilder;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class FFMpegTextProgressBar implements Closeable {

	private static final int DEFAULT_INTERVAL_MILLISECONDS = 500;

	// -------------------------------------------------------

	/**
	 * @author fagu
	 */
	public static class FFMpegTextProgressBarBuilder {

		private final Progress progress;

		private final TextProgressBarBuilder textProgressBarBuilder;

		private FFMpegTextProgressBarBuilder(Progress progress, TextProgressBarBuilder textProgressBarBuilder) {
			this.progress = progress;
			this.textProgressBarBuilder = textProgressBarBuilder;
		}

		/**
		 * @param numberOfFrames
		 * @return
		 */
		public FFMpegTextProgressBar progressByFrame(Integer numberOfFrames) {
			return progressByFrame(numberOfFrames, null);
		}

		/**
		 * @param numberOfFrames
		 * @param fileSize
		 * @return
		 */
		public FFMpegTextProgressBar progressByFrame(Integer numberOfFrames, Long fileSize) {
			return progressBy(progress -> {
				long startTime = System.currentTimeMillis();
				return textProgressBarBuilder
						.buildForScheduling(() -> 100 * progress.getFrame() / numberOfFrames)
						.percentInside(fileSize == null || fileSize.longValue() == 0 ? null : () -> (int)(progress.getSizeKb() * 102_400L / fileSize))
						.estimatedTimeOfArrival(() -> {
							int currentFrame = progress.getFrame();
							if(currentFrame > 0) {
								long diff = System.currentTimeMillis() - startTime;
								long milliseconds = numberOfFrames * diff / currentFrame - diff;
								return (int)(milliseconds / 1000L);
							}
							return null;
						})
						.refresh(DEFAULT_INTERVAL_MILLISECONDS)
						.schedule();

			});
		}

		/**
		 * @param duration
		 * @return
		 */
		public FFMpegTextProgressBar progressByDuration(Duration duration) {
			return progressByDuration(duration, null);
		}

		/**
		 * @param duration
		 * @param fileSize
		 * @return
		 */
		public FFMpegTextProgressBar progressByDuration(Duration duration, Long fileSize) {
			double durTotal = duration.toSeconds();
			return progressBy(progress -> {
				long startTime = System.currentTimeMillis();
				return textProgressBarBuilder
						.buildForScheduling(() -> {
							Time time = progress.getTime();
							if(time == null) {
								return 0;
							}
							double currentSeconds = time.toSeconds();
							return (int)(100 * currentSeconds / durTotal);
						})
						.percentInside(fileSize == null || fileSize.longValue() == 0 ? null : () -> (int)(progress.getSizeKb() * 102400L / fileSize))
						.estimatedTimeOfArrival(() -> {
							Time time = progress.getTime();
							if(time != null) {
								double currentSeconds = time.toSeconds();
								if(currentSeconds > 0) {
									double remainSeconds = durTotal - currentSeconds;
									int milliseconds = (int)(remainSeconds * (int)(System.currentTimeMillis() - startTime) / currentSeconds);
									return milliseconds / 1000;
								}
							}
							return null;
						})
						.refresh(DEFAULT_INTERVAL_MILLISECONDS)
						.schedule();
			});
		}

		// ***************************************************

		/**
		 * @param function
		 * @return
		 */
		private FFMpegTextProgressBar progressBy(Function<Progress, TextProgressBar> function) {
			return new FFMpegTextProgressBar(function.apply(progress));
		}
	}

	// -------------------------------------------------------

	private final TextProgressBar textProgressBar;

	/**
	 * @param textProgressBar
	 */
	private FFMpegTextProgressBar(TextProgressBar textProgressBar) {
		this.textProgressBar = textProgressBar;
	}

	/**
	 * @param executor
	 * @param consolePrefixMessage
	 * @return
	 */
	public static FFMpegTextProgressBarBuilder with(FFExecutor<Object> executor, String consolePrefixMessage) {
		return with(executor.getProgress(), consolePrefixMessage);
	}

	/**
	 * @param progress
	 * @param consolePrefixMessage
	 * @return
	 */
	public static FFMpegTextProgressBarBuilder with(Progress progress, String consolePrefixMessage) {
		final int PREFIX_WIDTH = 60;
		TextProgressBarBuilder textProgressBarBuilder = TextProgressBar.width(25)
				.consolePrefixMessage(StringUtils.rightPad(StringUtils.abbreviate(consolePrefixMessage, PREFIX_WIDTH), PREFIX_WIDTH) + "  ")
				.progressChars(Chars.done('=').inside('#'))
				.finishChars(Chars.done('#').head('#').remain('='));
		return new FFMpegTextProgressBarBuilder(progress, textProgressBarBuilder);
	}

	/**
	 * @param percent
	 */
	public void setPercent(int percent) {
		if(textProgressBar != null) {
			textProgressBar.printFull(percent);
		}
	}

	/**
	 * 
	 */
	@Override
	public void close() {
		if(textProgressBar != null) {
			textProgressBar.close();
		}
	}

}
