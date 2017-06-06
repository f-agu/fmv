package org.fagu.fmv.mymedia.utils;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.function.Consumer;

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
public class FFMpegTextProgressBar {

	private static final int DEFAULT_INTERVAL_MILLISECONDS = 500;

	private TextProgressBar textProgressBar;

	/**
	 *
	 */
	public FFMpegTextProgressBar() {}

	/**
	 * @param executor
	 * @param consolePrefixMessage
	 * @param consumer
	 */
	public void prepareProgressBar(FFExecutor<Object> executor, String consolePrefixMessage, Consumer<Progress> consumer) {
		prepareProgressBar(executor.getProgress(), consolePrefixMessage, consumer);
	}

	/**
	 * @param metadatas
	 * @param executor
	 * @param consolePrefixMessage
	 */
	public void prepareProgressBar(Progress progress, String consolePrefixMessage, Consumer<Progress> consumer) {
		if(progress == null) {
			return;
		}
		final int PREFIX_WIDTH = 60;
		textProgressBar = TextProgressBarBuilder.width(25)
				.consolePrefixMessage(StringUtils.rightPad(StringUtils.abbreviate(consolePrefixMessage, PREFIX_WIDTH), PREFIX_WIDTH) + "  ")
				.progressChars(Chars.done('=').inside('#'))
				.finishChars(Chars.done('#').head('#').remain('='))
				.build();
		consumer.accept(progress);
	}

	/**
	 * @param numberOfFrames
	 * @return
	 */
	public Consumer<Progress> progressByFrame(Integer numberOfFrames) {
		return progressByFrame(numberOfFrames, null);
	}

	/**
	 * @param numberOfFrames
	 * @param fileSize
	 * @return
	 */
	public Consumer<Progress> progressByFrame(Integer numberOfFrames, Long fileSize) {
		return progress -> {
			long startTime = System.currentTimeMillis();
			textProgressBar.schedule(() -> {
				int currentFrame = progress.getFrame();
				return 100 * currentFrame / numberOfFrames;
			}, fileSize == null || fileSize.longValue() == 0 ? null : () -> {
				return (int)(progress.getSizeKb() * 102400L / fileSize);
			}, () -> {
				int currentFrame = progress.getFrame();
				if(currentFrame > 0) {
					long diff = System.currentTimeMillis() - startTime;
					long milliseconds = numberOfFrames * diff / currentFrame - diff;
					return (int)(milliseconds / 1000L);
				}
				return null;
			}, DEFAULT_INTERVAL_MILLISECONDS);
		};
	}

	/**
	 * @param duration
	 * @return
	 */

	public Consumer<Progress> progressByDuration(Duration duration) {
		return progressByDuration(duration, null);
	}

	/**
	 * @param duration
	 * @param fileSize
	 * @return
	 */
	public Consumer<Progress> progressByDuration(Duration duration, Long fileSize) {
		double durTotal = duration.toSeconds();
		return progress -> {
			long startTime = System.currentTimeMillis();
			textProgressBar.schedule(() -> {
				Time time = progress.getTime();
				if(time == null) {
					return 0;
				}
				double currentSeconds = time.toSeconds();
				return (int)(100 * currentSeconds / durTotal);
			}, fileSize == null || fileSize.longValue() == 0 ? null : () -> {
				return (int)(progress.getSizeKb() * 102400L / fileSize);
			}, () -> {
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
			}, DEFAULT_INTERVAL_MILLISECONDS);
		};
	}

	/**
	 * @param percent
	 */
	public void close(int percent) {
		if(textProgressBar != null) {
			textProgressBar.close(percent);
		}
	}

	/**
	 *
	 */
	public void close() {
		if(textProgressBar != null) {
			textProgressBar.close();
		}
	}

}
