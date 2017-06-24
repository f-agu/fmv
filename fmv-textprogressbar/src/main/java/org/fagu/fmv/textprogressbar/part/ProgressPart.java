package org.fagu.fmv.textprogressbar.part;

import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class ProgressPart implements Part {

	// --------------------------------------------------------

	public static class ProgressPartBuilder {

		private final int width;

		private String leftBound = "[";

		private String rightBound = "]";

		private ProgressChar progressChar = new BasicProgressChar();

		/**
		 * @param width
		 */
		private ProgressPartBuilder(int width) {
			this.width = width;
		}

		/**
		 * @param leftBound
		 * @return
		 */
		public ProgressPartBuilder leftBound(String leftBound) {
			this.leftBound = leftBound;
			return this;
		}

		/**
		 * @param rightBound
		 * @return
		 */
		public ProgressPartBuilder rightBound(String rightBound) {
			this.rightBound = rightBound;
			return this;
		}

		/**
		 * @param progressChar
		 * @return
		 */
		public ProgressPartBuilder progressChars(ProgressChar progressChar) {
			if(progressChar != null) {
				this.progressChar = progressChar;
			}
			return this;
		}

		/**
		 * @return
		 */
		public ProgressPart build() {
			leftBound = StringUtils.defaultString(leftBound);
			rightBound = StringUtils.defaultString(rightBound);
			if(width <= leftBound.length() + rightBound.length()) {
				throw new IllegalArgumentException("Width is too short: " + width);
			}

			return new ProgressPart(this);
		}

	}

	// --------------------------------------------------------

	public enum CharType {
		DONE, HEAD, REMAIN
	}

	// --------------------------------------------------------

	public static interface ProgressChar {

		char getAt(int position, int headJunction, int width, CharType charType);
	}

	// --------------------------------------------------------

	public static class BasicProgressChar implements ProgressChar {

		private final char done;

		private final char head;

		private final char remain;

		public BasicProgressChar() {
			this('=', '>', ' ');
		}

		public BasicProgressChar(char done, char head, char remain) {
			this.done = done;
			this.head = head;
			this.remain = remain;
		}

		@Override
		public char getAt(int position, int headJunction, int width, CharType charType) {
			if(charType == CharType.DONE) {
				return done; // =
			}
			if(charType == CharType.HEAD) {
				return head; // >
			}
			return remain; // ' '
		}
	}

	// --------------------------------------------------------

	public static class InsideProgressChar extends BasicProgressChar {

		private final Supplier<Integer> percentInsideSupplier;

		private final char inside;

		public InsideProgressChar(Supplier<Integer> percentInsideSupplier) {
			super();
			this.percentInsideSupplier = percentInsideSupplier;
			this.inside = '#';
		}

		public InsideProgressChar(Supplier<Integer> percentInsideSupplier, char done, char head, char remain, char inside) {
			super(done, head, remain);
			this.percentInsideSupplier = percentInsideSupplier;
			this.inside = inside;
		}

		@Override
		public char getAt(int position, int headJunction, int width, CharType charType) {
			if(charType == CharType.DONE) {
				Integer percentInside = percentInsideSupplier.get();
				int insideJunction = 0;
				if(percentInside != null) {
					insideJunction = percentInside.intValue() * width / 100;
					insideJunction = Math.min(insideJunction, headJunction);
				}
				if(position < insideJunction) {
					return inside; // #
				}
			}
			return super.getAt(position, headJunction, width, charType);
		}
	}

	// --------------------------------------------------------

	private final int width;

	private final String leftBound;

	private final String rightBound;

	private final ProgressChar progressChar;

	private ProgressPart(ProgressPartBuilder builder) {
		this.width = builder.width;
		this.leftBound = builder.leftBound;
		this.rightBound = builder.rightBound;
		this.progressChar = builder.progressChar;
	}

	/**
	 * @param width
	 * @return
	 */
	public static ProgressPartBuilder width(int width) {
		return new ProgressPartBuilder(width);
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(org.fagu.fmv.textprogressbar.ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		StringBuilder buf = new StringBuilder(width);
		buf.append(leftBound);
		int w = width - leftBound.length() - rightBound.length();
		int headJunction = status.getPercent() * w / 100;
		CharType type = null;
		for(int i = 0; i < w; ++i) {
			if(i < headJunction) {
				type = CharType.DONE;
			} else if(i == headJunction) {
				type = CharType.HEAD;
			} else {
				type = CharType.REMAIN;
			}
			buf.append(progressChar.getAt(i, headJunction, w, type));
		}
		buf.append(rightBound);
		return buf.toString();
	}

}
