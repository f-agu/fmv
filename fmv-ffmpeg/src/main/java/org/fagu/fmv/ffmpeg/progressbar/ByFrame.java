package org.fagu.fmv.ffmpeg.progressbar;

import java.util.function.IntSupplier;

import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.part.ETAPart;


/**
 * @author fagu
 */
public class ByFrame extends By {

	// -----------------------------------------------------

	public static class ByFrameBuilder extends ByBuilder {

		private final int numberOfFrames;

		ByFrameBuilder(Progress progress, int numberOfFrames) {
			super(progress);
			this.numberOfFrames = numberOfFrames;
		}

		public ByFrameBuilder fileSize(long size) {
			this.fileSize = size;
			return this;
		}

		public ByFrame build() {
			return new ByFrame(this);
		}

	}

	// -----------------------------------------------------

	private final int numberOfFrames;

	ByFrame(ByFrameBuilder builder) {
		super(builder);
		this.numberOfFrames = builder.numberOfFrames;
	}

	/**
	 * @return
	 */
	@Override
	public IntSupplier progressInPercent() {
		return () -> 100 * progress.getFrame() / numberOfFrames;
	}

	/**
	 * @return
	 */
	@Override
	public Part etaPart() {
		long startTime = System.currentTimeMillis();
		return new ETAPart(status -> {
			int currentFrame = progress.getFrame();
			if(currentFrame > 0) {
				long diff = System.currentTimeMillis() - startTime;
				long milliseconds = numberOfFrames * diff / currentFrame - diff;
				return (int)(milliseconds / 1000L);
			}
			return null;
		});
	}
}
