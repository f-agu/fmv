package org.fagu.fmv.ffmpeg.progressbar;

import java.util.Objects;
import java.util.function.IntSupplier;

import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.part.ETAPart;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author fagu
 */
public class ByDuration extends By {

	private final Duration duration;

	// -----------------------------------------------------

	public static class ByDurationBuilder extends ByBuilder {

		private final Duration duration;

		ByDurationBuilder(Progress progress, Duration duration) {
			super(progress);
			this.duration = Objects.requireNonNull(duration);
		}

		public ByDuration build() {
			return new ByDuration(this);
		}

	}

	// -----------------------------------------------------

	/**
	 * @param progress
	 * @param duration
	 * @param fileSize
	 */
	ByDuration(ByDurationBuilder builder) {
		super(builder);
		this.duration = builder.duration;
	}

	/**
	 * @return
	 */
	@Override
	public IntSupplier progressInPercent() {
		double durTotal = duration.toSeconds();
		return () -> {
			Time time = progress.getTime();
			if(time == null) {
				return 0;
			}
			double currentSeconds = time.toSeconds();
			return (int)(100 * currentSeconds / durTotal);
		};
	}

	/**
	 * @return
	 */
	@Override
	public Part etaPart() {
		double durTotal = duration.toSeconds();
		long startTime = System.currentTimeMillis();
		return new ETAPart(status -> {
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
		});
	}
}
