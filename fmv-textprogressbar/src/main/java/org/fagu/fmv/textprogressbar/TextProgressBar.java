package org.fagu.fmv.textprogressbar;

/*-
 * #%L
 * fmv-textprogressbar
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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

		private Consumer<String> display = System.out::print;

		private String startCommandSequence = "\r";

		private TextProgressBarBuilder() {}

		public TextProgressBarBuilder withStartCommandSequence(String value) {
			if(value != null) {
				this.startCommandSequence = value;
			}
			return this;
		}

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

		public WidthFor fixWidth(int width) {
			return new WidthFor(this, width);
		}

		public TextProgressBarBuilder autoPrintFull(boolean autoPrintFull) {
			this.autoPrintFull = autoPrintFull;
			return this;
		}

		public TextProgressBarBuilder displayTo(Consumer<String> display) {
			if(display != null) {
				this.display = display;
			}
			return this;
		}

		public TextProgressBar buildAndSchedule() {
			return buildAndSchedule(null);
		}

		public TextProgressBar buildAndSchedule(IntSupplier progressInPercent) {
			if(parts.isEmpty()) {
				throw new IllegalStateException("No part defined !");
			}
			AtomicLong refresh = new AtomicLong(Long.MAX_VALUE);
			for(Part part : parts) {
				part.getRefreshInMilliseconds().ifPresent(d -> refresh.updateAndGet(l -> Math.min(l, d)));
			}
			long delay = refresh.get() == Long.MAX_VALUE ? 500 : refresh.get();
			return buildAndSchedule(progressInPercent, delay);
		}

		public TextProgressBar buildAndSchedule(IntSupplier progressInPercent, long refreshPeriodMilliseconds) {
			TextProgressBar bar = new TextProgressBar(this);
			bar.schedule(progressInPercent, refreshPeriodMilliseconds);
			return bar;
		}

		public TextProgressBar buildForPrinting() {
			return new TextProgressBar(this);
		}

	}

	// --------------------------------------------------------

	public static class WidthFor {

		private final TextProgressBarBuilder builder;

		private final int width;

		private BiFunction<String, Integer, String> pad = FixWidthPart.RIGHT_PAD;

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

	private final Consumer<String> display;

	private final String startCommandSequence;

	private TimerTask timerTask;

	private Timer timer;

	private TextProgressBar(TextProgressBarBuilder builder) {
		this.autoPrintFull = builder.autoPrintFull;
		this.display = builder.display;
		this.startCommandSequence = builder.startCommandSequence;
		this.parts = Collections.unmodifiableList(new ArrayList<>(builder.parts));
	}

	public static TextProgressBarBuilder newBar() {
		return new TextProgressBarBuilder();
	}

	public void print(int percent) {
		print(false, percent);
	}

	public TextProgressBar printFull() {
		return printFull(100);
	}

	public TextProgressBar printFull(int percent) {
		return print(true, percent);
	}

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

	private void schedule(IntSupplier progressInPercent, long refreshPeriodMilliseconds) {
		IntSupplier supplier = progressInPercent != null ? progressInPercent : () -> 0;
		timerTask = new TimerTask() {

			@Override
			public void run() {
				print(supplier.getAsInt());
			}
		};
		timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 0, refreshPeriodMilliseconds);
	}

	private TextProgressBar print(boolean finish, int percent) {
		ProgressStatus status = createStatus(finish, percent);

		StringBuilder bar = new StringBuilder(140);
		bar.append(startCommandSequence);
		parts.stream().forEach(p -> bar.append(p.getWith(status)));
		display.accept(bar.toString());
		return this;
	}

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
