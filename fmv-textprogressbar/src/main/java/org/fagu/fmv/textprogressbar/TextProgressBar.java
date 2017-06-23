package org.fagu.fmv.textprogressbar;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

import org.fagu.fmv.textprogressbar.part.ETAPart;
import org.fagu.fmv.textprogressbar.part.FixWidthPart;
import org.fagu.fmv.textprogressbar.part.TextPart;


/**
 * @author fagu
 */
public class TextProgressBar implements Closeable {

	// --------------------------------------------------------

	public static class TextProgressBarBuilder {

		private boolean autoPrintFull = true;

		private final List<Part> parts = new ArrayList<>();

		private TextProgressBarBuilder() {}

		public TextProgressBarBuilder append(Part part) {
			if(part != null) {
				parts.add(part);
			}
			return this;
		}

		public TextProgressBarBuilder appendText(String text) {
			return append(new TextPart(text));
		}

		public TextProgressBarBuilder appendETA(Function<ProgressStatus, Integer> etaInSecondsSupplier) {
			return append(new ETAPart(etaInSecondsSupplier));
		}

		public TextProgressBarBuilder appendTime(Function<ProgressStatus, Integer> etaInSecondsSupplier) {
			return append(new ETAPart(etaInSecondsSupplier));
		}

		public WidthFor width(int width) {
			return new WidthFor(this, width);
		}

		public TextProgressBarBuilder autoPrintFull(boolean autoPrintFull) {
			this.autoPrintFull = autoPrintFull;
			return this;
		}

		public TextProgressBar scheduling(IntSupplier progressInPercent) {
			return scheduling(progressInPercent, 500);
		}

		public TextProgressBar scheduling(IntSupplier progressInPercent, long refreshPeriodMilliseconds) {
			return new TextProgressBar(this, progressInPercent, refreshPeriodMilliseconds);
		}

		public TextProgressBar buildForPrinting() {
			return new TextProgressBar(this, null, - 1);
		}

	}

	// --------------------------------------------------------

	public static class WidthFor {

		private final TextProgressBarBuilder builder;

		private final int width;

		private BiFunction<String, Integer, String> pad = FixWidthPart.LEFT_PAD;

		private WidthFor(TextProgressBarBuilder builder, int width) {
			this.builder = builder;
			this.width = width;
		}

		public WidthFor pad(BiFunction<String, Integer, String> pad) {
			if(pad != null) {
				this.pad = pad;
			}
			return this;
		}

		public WidthFor leftPad() {
			return pad(FixWidthPart.LEFT_PAD);
		}

		public WidthFor rightPad() {
			return pad(FixWidthPart.RIGHT_PAD);
		}

		public WidthFor centerPad() {
			return pad(FixWidthPart.CENTER_PAD);
		}

		public TextProgressBarBuilder with(Part part) {
			return builder.append(new FixWidthPart(part, width, pad));
		}

		public TextProgressBarBuilder withText(String text) {
			return with(new TextPart(text));
		}

		public TextProgressBarBuilder withETA(Function<ProgressStatus, Integer> etaInSecondsSupplier) {
			return with(new ETAPart(etaInSecondsSupplier));
		}

	}

	// --------------------------------------------------------

	private final boolean autoPrintFull;

	private final List<Part> parts;

	private TimerTask timerTask;

	private Timer timer;

	/**
	 * @param builder
	 * @param progressInPercent
	 * @param refreshPeriodMilliseconds
	 */
	private TextProgressBar(TextProgressBarBuilder builder, IntSupplier progressInPercent, long refreshPeriodMilliseconds) {
		this.autoPrintFull = builder.autoPrintFull;
		this.parts = Collections.unmodifiableList(new ArrayList<>(builder.parts));

		if(progressInPercent != null && refreshPeriodMilliseconds > 0) {
			schedule(progressInPercent, refreshPeriodMilliseconds);
		}
	}

	/**
	 * @return
	 */
	public static TextProgressBarBuilder newBar() {
		return new TextProgressBarBuilder();
	}

	/**
	 * @param percent
	 */
	public void print(int percent) {
		print(false, percent);
	}

	/**
	 *
	 */
	public TextProgressBar printFull() {
		return printFull(100);
	}

	/**
	 * @param percent
	 */
	public TextProgressBar printFull(int percent) {
		return print(true, percent);
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if(autoPrintFull) {
			printFull();
		}
		if(timerTask != null) {
			timerTask.cancel();
		}
		if(timer != null) {
			timer.purge();
			timer.cancel();
		}
	}

	// **********************************************

	/**
	 * @param progressInPercent
	 * @param refreshPeriodMilliseconds
	 */
	private void schedule(IntSupplier progressInPercent, long refreshPeriodMilliseconds) {
		timerTask = new TimerTask() {

			/**
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run() {
				print(progressInPercent.getAsInt());
			}
		};
		timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 0, refreshPeriodMilliseconds);
	}

	/**
	 * @param finish
	 * @param percent
	 * @return
	 */
	private TextProgressBar print(boolean finish, int percent) {
		ProgressStatus status = createStatus(finish, percent);

		StringBuilder bar = new StringBuilder(140);
		bar.append('\r');
		parts.stream().forEach(p -> bar.append(p.getWith(status)));
		System.out.println(bar);

		return this;
	}

	/**
	 * @param finish
	 * @param percent
	 * @return
	 */
	private ProgressStatus createStatus(boolean finish, int percent) {
		return new ProgressStatus() {

			@Override
			public boolean isFinished() {
				return finish;
			}

			@Override
			public int getPercent() {
				return percent;
			}
		};
	}

}
