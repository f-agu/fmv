package org.fagu.fmv.ffmpeg.progressbar;

import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.TextProgressBar.TextProgressBarBuilder;
import org.fagu.fmv.textprogressbar.part.FinishedOrNotPart;
import org.fagu.fmv.textprogressbar.part.ProgressPart;
import org.fagu.fmv.textprogressbar.part.ProgressPart.InsideProgressChar;
import org.fagu.fmv.textprogressbar.part.SpendTimePart;


/**
 * @author fagu
 */
public abstract class By {

	protected final Progress progress;

	protected final Long fileSize;

	// -----------------------------------------------------

	public static class ByBuilder {

		protected final Progress progress;

		protected Long fileSize;

		protected ByBuilder(Progress progress) {
			this.progress = Objects.requireNonNull(progress);
		}

	}

	// -----------------------------------------------------

	By(ByBuilder builder) {
		this.progress = builder.progress;
		this.fileSize = builder.fileSize;
	}

	/**
	 * @return
	 */
	public Part progressPart() {
		Supplier<Integer> percentInsideSupplier = fileSize == null || fileSize.longValue() == 0 ? null
				: () -> (int)(progress.getSizeKb() * 102400L / fileSize);
		return ProgressPart.width(32)
				.progressChars(new InsideProgressChar(percentInsideSupplier))
				.build();
	}

	/**
	 * @return
	 */
	abstract IntSupplier progressInPercent();

	/**
	 * @return
	 */
	abstract Part etaPart();

	/**
	 * @param text
	 * @return
	 */
	public TextProgressBar makeBar(String text) {
		TextProgressBarBuilder builder = TextProgressBar.newBar();
		return builder.fixWidth(60)
				.withText(text)
				.append(progressPart())
				.fixWidth(11)
				.with(new FinishedOrNotPart(etaPart(), new SpendTimePart()))
				.buildAndSchedule(progressInPercent());

	}

}
