/**
 * 
 */
package org.fagu.fmv.textprogressbar.part;

import java.util.Objects;
import java.util.function.Supplier;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class ProgressPart implements Part {

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
	 * @author fagu
	 */
	public static class ProgressPartBuilder {

		private int width;

		private Chars progressChars = Chars.done('=');

		private Chars finishChars = progressChars;

		private char leftBound = '[';

		private char rightBound = ']';

		private Supplier<Integer> percentInsideSupplier;

		/**
		 * @param progressWidth
		 */
		private ProgressPartBuilder(int progressWidth) {
			this.width = progressWidth;
		}

		/**
		 * @param leftBound
		 * @return
		 */
		public ProgressPartBuilder leftBound(char leftBound) {
			this.leftBound = leftBound;
			return this;
		}

		/**
		 * @param rightBound
		 * @return
		 */
		public ProgressPartBuilder rightBound(char rightBound) {
			this.rightBound = rightBound;
			return this;
		}

		/**
		 * @param done
		 * @param remain
		 * @return
		 */
		public ProgressPartBuilder progressChars(char done, char remain) {
			return progressChars(Chars.done(done).remain(remain));
		}

		/**
		 * @param done
		 * @param head
		 * @param remain
		 * @return
		 */
		public ProgressPartBuilder progressChars(char done, char head, char remain) {
			return progressChars(Chars.done(done).head(head).remain(remain));
		}

		/**
		 * @param chars
		 * @return
		 */
		public ProgressPartBuilder progressChars(Chars chars) {
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
		public ProgressPartBuilder finishChars(char done, char remain) {
			return finishChars(Chars.done(done).remain(remain));
		}

		/**
		 * @param done
		 * @param head
		 * @param remain
		 * @return
		 */
		public ProgressPartBuilder finishChars(char done, char head, char remain) {
			return finishChars(Chars.done(done).head(head).remain(remain));
		}

		/**
		 * @param chars
		 * @return
		 */
		public ProgressPartBuilder finishChars(Chars chars) {
			if(chars != null) {
				finishChars = Objects.requireNonNull(chars);
			}
			return this;
		}

		/**
		 * @param percentInside
		 * @return
		 */
		public ProgressPartBuilder percentInside(Supplier<Integer> percentInside) {
			this.percentInsideSupplier = percentInside;
			return this;
		}

		/**
		 * @return
		 */
		public ProgressPart build() {
			return new ProgressPart(this);
		}

	}

	// --------------------------------------------------------

	private final int width;

	private final Chars progressChars;

	private final Chars finishChars;

	private final char leftBound;

	private final char rightBound;

	private final Supplier<Integer> percentInsideSupplier;

	private ProgressPart(ProgressPartBuilder builder) {
		this.width = builder.width;
		this.progressChars = builder.progressChars;
		this.finishChars = builder.finishChars;
		this.leftBound = builder.leftBound;
		this.rightBound = builder.rightBound;
		this.percentInsideSupplier = builder.percentInsideSupplier;
	}

	/**
	 * @param progressWidth
	 * @return
	 */
	public static ProgressPartBuilder width(int progressWidth) {
		return new ProgressPartBuilder(progressWidth);
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(org.fagu.fmv.textprogressbar.ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		Chars chars = status.isFinished() ? finishChars : progressChars;

		StringBuilder buf = new StringBuilder(width);
		buf.append(leftBound);
		int junction1 = status.getPercent() * width / 100;
		int junction2 = 0;
		Integer percentInside = percentInsideSupplier.get();
		if(percentInside != null) {
			junction2 = percentInside.intValue() * width / 100;
			junction2 = Math.min(junction2, junction1);
		}
		for(int i = 0; i < width; ++i) {
			if(i < junction2) {
				buf.append(chars.inside); // #
			} else if(i < junction1) {
				buf.append(chars.done); // =
			} else if(i == junction1) {
				buf.append(chars.head); // >
			} else {
				buf.append(chars.remain); // ' '
			}
		}
		buf.append(rightBound);
		return buf.toString();
	}

}
