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

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class TextProgressBar {

	private final Supplier<String> consolePrefixMessage;

	private final int progressWidth;

	private char leftBound = '[';

	private char rightBound = ']';

	private final Chars progressChars, finishChars;

	private Timer timer;

	private TimerTask timerTask;

	private final boolean autoPrintFull;

	private long startTime;

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

		private Supplier<String> consolePrefixMessage = () -> "";

		private boolean autoPrintFull = true;

		private Chars progressChars, finishChars;

		/**
		 * @param progressWidth
		 */
		private TextProgressBarBuilder(int progressWidth) {
			this.progressWidth = progressWidth;
		}

		/**
		 * @param progressWidth
		 * @return
		 */
		public static TextProgressBarBuilder width(int progressWidth) {
			return new TextProgressBarBuilder(progressWidth);
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
			this.consolePrefixMessage = Objects.requireNonNull(consolePrefixMessage);
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
			progressChars = Objects.requireNonNull(chars);
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
			finishChars = Objects.requireNonNull(chars);
			return this;
		}

		/**
		 * @return
		 */
		public TextProgressBar build() {
			if(progressChars == null) {
				progressChars = Chars.done('=');
			}
			if(finishChars == null) {
				finishChars = Chars.done('=');
			}
			return new TextProgressBar(progressWidth, consolePrefixMessage, autoPrintFull, progressChars, finishChars);
		}
	}

	// --------------------------------------------------------

	/**
	 * @param progressWidth
	 * @param consolePrefixMessage
	 * @param autoPrintFull
	 * @param progressChars
	 * @param finishChars
	 */
	private TextProgressBar(int progressWidth, Supplier<String> consolePrefixMessage, boolean autoPrintFull, Chars progressChars, Chars finishChars) {
		this.progressWidth = progressWidth;
		this.consolePrefixMessage = consolePrefixMessage;
		this.autoPrintFull = autoPrintFull;
		this.progressChars = progressChars;
		this.finishChars = finishChars;
	}

	/**
	 * @param percent
	 * @param etaInSeconds
	 */
	public void schedule(IntSupplier percent, Supplier<Integer> etaInSeconds) {
		schedule(percent, null, etaInSeconds, 500);
	}

	/**
	 * @param percent
	 * @param etaInSeconds
	 * @param periodMilliseconds
	 */
	public void schedule(IntSupplier percent, Supplier<Integer> etaInSeconds, long periodMilliseconds) {
		schedule(percent, null, etaInSeconds, periodMilliseconds);
	}

	/**
	 * @param percent
	 * @param percentInside
	 * @param etaInSeconds
	 * @param periodMilliseconds
	 */
	public void schedule(IntSupplier percent, IntSupplier percentInside, Supplier<Integer> etaInSeconds, long periodMilliseconds) {
		if(timerTask != null) {
			throw new IllegalStateException("Already called !");
		}
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
		timer.scheduleAtFixedRate(timerTask, 0, periodMilliseconds);
		startTime = System.currentTimeMillis();
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
	public void print(int percent, Integer percentInside, Integer etaInSeconds) {
		print(false, percent, percentInside, etaInSeconds, progressChars);
	}

	/**
	 *
	 */
	public void printFull() {
		printFull(100);
	}

	/**
	 * @param percent
	 * @param percentInside
	 */
	public void printFull(int percent, Integer percentInside) {
		print(true, percent, percentInside, null, finishChars);
	}

	/**
	 * @param percent
	 */
	public void printFull(int percent) {
		print(true, percent, null, null, finishChars);
	}

	/**
	 *
	 */
	public void close() {
		close(100);
	}

	/**
	 * @param percent
	 */
	public void close(int percent) {
		if(autoPrintFull) {
			printFull(percent);
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
	 * @param finish
	 * @param percent
	 * @param percentInside
	 * @param etaInSeconds
	 * @param chars
	 */
	private void print(boolean finish, int percent, Integer percentInside, Integer etaInSeconds, Chars chars) {
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
	}
}
