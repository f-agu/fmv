package org.fagu.fmv.mymedia.utils;

import java.io.Closeable;

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

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class TextProgressBar implements Closeable {

	// --------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class Chars {

		private char inside;

		private char done;

		private char head;

		private char remain;

		/**
		 * @param done
		 */
		private Chars(char done) {
			this.done = done;
			this.inside = done;
			head = '>';
			remain = ' ';
		}

		/**
		 * @param done
		 * @return
		 */
		public static Chars done(char done) {
			return new Chars(done);
		}

		/**
		 * @return
		 */
		public Chars inside(char inside) {
			this.inside = inside;
			return this;
		}

		/**
		 * @param head
		 * @return
		 */
		public Chars head(char head) {
			this.head = head;
			return this;
		}

		/**
		 * @param remain
		 * @return
		 */
		public Chars remain(char remain) {
			this.remain = remain;
			return this;
		}
	}

	// --------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class TextProgressBarBuilder {

		private int progressWidth;

		private Supplier<String> consolePrefixMessage = () -> StringUtils.EMPTY;

		private boolean autoPrintFull = true;

		private Chars progressChars = Chars.done('=');

		private Chars finishChars = progressChars;

		private char leftBound = '[';

		private char rightBound = ']';

		/**
		 * @param progressWidth
		 */
		private TextProgressBarBuilder(int progressWidth) {
			this.progressWidth = progressWidth;
		}

		/**
		 * @param leftBound
		 * @return
		 */
		public TextProgressBarBuilder leftBound(char leftBound) {
			this.leftBound = leftBound;
			return this;
		}

		/**
		 * @param rightBound
		 * @return
		 */
		public TextProgressBarBuilder rightBound(char rightBound) {
			this.rightBound = rightBound;
			return this;
		}

		/**
		 * @param consolePrefixMessage
		 * @return
		 */
		public TextProgressBarBuilder consolePrefixMessage(String consolePrefixMessage) {
			Objects.requireNonNull(consolePrefixMessage);
			return consolePrefixMessage(() -> consolePrefixMessage);
		}

		/**
		 * @param consolePrefixMessage
		 * @return
		 */
		public TextProgressBarBuilder consolePrefixMessage(Supplier<String> consolePrefixMessage) {
			this.consolePrefixMessage = Objects.requireNonNull(consolePrefixMessage);
			return this;
		}

		/**
		 * @param autoPrintFull
		 * @return
		 */
		public TextProgressBarBuilder autoPrintFull(boolean autoPrintFull) {
			this.autoPrintFull = autoPrintFull;
			return this;
		}

		/**
		 * @param done
		 * @param remain
		 * @return
		 */
		public TextProgressBarBuilder progressChars(char done, char remain) {
			return progressChars(Chars.done(done).remain(remain));
		}

		/**
		 * @param done
		 * @param head
		 * @param remain
		 * @return
		 */
		public TextProgressBarBuilder progressChars(char done, char head, char remain) {
			return progressChars(Chars.done(done).head(head).remain(remain));
		}

		/**
		 * @param chars
		 * @return
		 */
		public TextProgressBarBuilder progressChars(Chars chars) {
			if(chars != null) {
				progressChars = Objects.requireNonNull(chars);
			}
			return this;
		}

		/**
		 * @param done
		 * @param remain
		 * @return
		 */
		public TextProgressBarBuilder finishChars(char done, char remain) {
			return finishChars(Chars.done(done).remain(remain));
		}

		/**
		 * @param done
		 * @param head
		 * @param remain
		 * @return
		 */
		public TextProgressBarBuilder finishChars(char done, char head, char remain) {
			return finishChars(Chars.done(done).head(head).remain(remain));
		}

		/**
		 * @param chars
		 * @return
		 */
		public TextProgressBarBuilder finishChars(Chars chars) {
			if(chars != null) {
				finishChars = Objects.requireNonNull(chars);
			}
			return this;
		}

		/**
		 * @param percent
		 * @return
		 */
		public ScheduleBuilder buildForScheduling(IntSupplier percent) {
			return new ScheduleBuilder(this, percent);
		}

		/**
		 * @return
		 */
		public TextProgressBar buildForPrinting() {
			return new TextProgressBar(this);
		}

	}

	// --------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class ScheduleBuilder {

		private final TextProgressBarBuilder textProgressBarBuilder;

		private final IntSupplier percent;

		private Supplier<Integer> etaInSeconds;

		private long periodMilliseconds = 500;

		private IntSupplier percentInside;

		private ScheduleBuilder(TextProgressBarBuilder textProgressBarBuilder, IntSupplier percent) {
			this.textProgressBarBuilder = textProgressBarBuilder;
			this.percent = Objects.requireNonNull(percent);
		}

		public ScheduleBuilder estimatedTimeOfArrival(Supplier<Integer> etaInSeconds) {
			this.etaInSeconds = etaInSeconds;
			return this;
		}

		public ScheduleBuilder refresh(long periodMilliseconds) {
			if(periodMilliseconds < 1) {
				throw new IllegalArgumentException("refresh must be positive: " + periodMilliseconds);
			}
			this.periodMilliseconds = periodMilliseconds;
			return this;
		}

		public ScheduleBuilder percentInside(IntSupplier percentInside) {
			this.percentInside = percentInside;
			return this;
		}

		@SuppressWarnings("resource")
		public TextProgressBar schedule() {
			return new TextProgressBar(textProgressBarBuilder).schedule(this);
		}

	}

	// --------------------------------------------------------

	private final Supplier<String> consolePrefixMessage;

	private final int progressWidth;

	private final char leftBound;

	private final char rightBound;

	private final Chars progressChars;

	private final Chars finishChars;

	private Timer timer;

	private TimerTask timerTask;

	private final boolean autoPrintFull;

	private long startTime;

	/**
	 * @param textProgressBarBuilder
	 */
	private TextProgressBar(TextProgressBarBuilder textProgressBarBuilder) {
		this.progressWidth = textProgressBarBuilder.progressWidth;
		this.consolePrefixMessage = textProgressBarBuilder.consolePrefixMessage;
		this.autoPrintFull = textProgressBarBuilder.autoPrintFull;
		this.progressChars = textProgressBarBuilder.progressChars;
		this.finishChars = textProgressBarBuilder.finishChars;
		this.leftBound = textProgressBarBuilder.leftBound;
		this.rightBound = textProgressBarBuilder.rightBound;
	}

	/**
	 * @param progressWidth
	 * @return
	 */
	public static TextProgressBarBuilder width(int progressWidth) {
		return new TextProgressBarBuilder(progressWidth);
	}

	/**
	 * @param percent
	 * @param etaInSeconds
	 */
	public void print(int percent, Integer etaInSeconds) {
		print(false, percent, null, etaInSeconds, progressChars);
	}

	/**
	 * @param percent
	 * @param percentInside
	 * @param etaInSeconds
	 */
	public TextProgressBar print(int percent, Integer percentInside, Integer etaInSeconds) {
		return print(false, percent, percentInside, etaInSeconds, progressChars);
	}

	/**
	 *
	 */
	public TextProgressBar printFull() {
		return printFull(100);
	}

	/**
	 * @param percent
	 * @param percentInside
	 */
	public TextProgressBar printFull(int percent, Integer percentInside) {
		return print(true, percent, percentInside, null, finishChars);
	}

	/**
	 * @param percent
	 */
	public TextProgressBar printFull(int percent) {
		return print(true, percent, null, null, finishChars);
	}

	/**
	 *
	 */
	@Override
	public void close() {
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

	// ************************************************

	/**
	 * @param scheduleBuilder
	 * @return
	 */
	private TextProgressBar schedule(ScheduleBuilder scheduleBuilder) {
		if(timerTask != null) {
			throw new IllegalStateException("Already called !");
		}
		Supplier<Integer> etaInSeconds = scheduleBuilder.etaInSeconds;
		IntSupplier percent = scheduleBuilder.percent;
		IntSupplier percentInside = scheduleBuilder.percentInside;
		timerTask = new TimerTask() {

			/**
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run() {
				Integer etaS = null;
				if(etaInSeconds != null) {
					etaS = etaInSeconds.get();
				}
				print(percent.getAsInt(), percentInside != null ? percentInside.getAsInt() : null, etaS);
			}
		};
		timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 0, scheduleBuilder.periodMilliseconds);
		startTime = System.currentTimeMillis();
		return this;
	}

	/**
	 * @param finish
	 * @param percent
	 * @param percentInside
	 * @param etaInSeconds
	 * @param chars
	 */
	private TextProgressBar print(boolean finish, int percent, Integer percentInside, Integer etaInSeconds, Chars chars) {
		String eta = null;
		if(etaInSeconds != null && etaInSeconds != 0) {
			int seconds = etaInSeconds;
			eta = StringUtils.leftPad(Integer.toString(seconds / 60), 2, '0') + ':' + StringUtils.leftPad(Integer.toString(seconds % 60), 2, '0');
		}

		StringBuilder bar = new StringBuilder(140);
		bar.append('\r').append(consolePrefixMessage.get()).append(leftBound);
		int junction1 = percent * progressWidth / 100;
		int junction2 = 0;
		if(percentInside != null) {
			junction2 = percentInside.intValue() * progressWidth / 100;
			junction2 = Math.min(junction2, junction1);
		}
		for(int i = 0; i < progressWidth; ++i) {
			if(i < junction2) {
				bar.append(chars.inside); // #
			} else if(i < junction1) {
				bar.append(chars.done); // =
			} else if(i == junction1) {
				bar.append(chars.head); // >
			} else {
				bar.append(chars.remain); // ' '
			}
		}
		bar.append(rightBound);
		if(finish) {
			long endTime = System.currentTimeMillis();
			long diffSec = (endTime - startTime) / 1000;
			int seconds = (int)(diffSec % 60L);
			int minutes = (int)(diffSec / 60L);
			bar.append(StringUtils.leftPad(Integer.toString(minutes), 4)).append(':');
			bar.append(StringUtils.leftPad(Integer.toString(seconds), 2, '0')).append("  ");
		} else {
			bar.append("  ").append(StringUtils.leftPad(Integer.toString(percent), 2)).append("%  ");
		}
		if(eta != null) {
			bar.append(eta).append(" ETA  ");
		}
		System.out.print(bar.toString());
		return this;
	}
}
