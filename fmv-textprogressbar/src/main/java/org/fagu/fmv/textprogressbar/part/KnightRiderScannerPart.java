package org.fagu.fmv.textprogressbar.part;

import java.util.Arrays;
import java.util.Iterator;
import java.util.OptionalLong;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class KnightRiderScannerPart implements Part {

	private static final String DEFAULT_SYMBOLS = "░▒▓█";

	private static final char DEFAULT_EMPTY = ' ';

	private static final long DEFAULT_REFRESH_MS = 50;

	private static final RightToLeftSymbol LEFT_2_RIGHT = new RightToLeftSymbol();

	private static final LeftToRightSymbol RIGHT_2_LEFT = new LeftToRightSymbol();

	private volatile int position;

	private Symbol symbol = new StartLeftToRightSymbol();

	private final char[] chars;

	private final char[] reversedChars;

	private final char emptyChar;

	private final int width;

	private final Long refreshInMilliseconds;

	public KnightRiderScannerPart(int width) {
		this(width, DEFAULT_SYMBOLS, DEFAULT_EMPTY, DEFAULT_REFRESH_MS);
	}

	public KnightRiderScannerPart(int width, String symbols, char emptyChar, Long refreshInMilliseconds) {
		this.width = assertWidth(width);
		this.chars = symbols.toCharArray();
		this.reversedChars = reverse(chars);
		this.emptyChar = emptyChar;
		this.refreshInMilliseconds = refreshInMilliseconds;
	}

	private KnightRiderScannerPart(KnightRiderScannerPartBuilder builder) {
		this.width = assertWidth(builder.width);
		this.chars = builder.symbols.toCharArray();
		this.reversedChars = reverse(this.chars);
		this.emptyChar = builder.emptyChar;
		this.refreshInMilliseconds = builder.refreshInMilliseconds;
	}

	public static KnightRiderScannerPartBuilder withWidth(int width) {
		return new KnightRiderScannerPartBuilder(assertWidth(width));
	}

	@Override
	public String getWith(ProgressStatus status) {
		if(status.isFinished()) {
			char[] ss = new char[width];
			Arrays.fill(ss, emptyChar);
			return new String(ss);
		}
		Next next = symbol.next(position, width, chars, reversedChars, emptyChar);
		position = next.position;
		symbol = next.symbol;
		return next.chars();
	}

	@Override
	public OptionalLong getRefreshInMilliseconds() {
		return refreshInMilliseconds != null ? OptionalLong.of(refreshInMilliseconds) : null;
	}

	// -----------------------------------------------------

	public static class KnightRiderScannerPartBuilder {

		private final int width;

		private String symbols = DEFAULT_SYMBOLS;

		private char emptyChar = DEFAULT_EMPTY;

		private Long refreshInMilliseconds = DEFAULT_REFRESH_MS;

		private KnightRiderScannerPartBuilder(int width) {
			this.width = width;
		}

		public KnightRiderScannerPartBuilder withSymbols(String symbols) {
			if(StringUtils.isNotEmpty(symbols)) {
				this.symbols = symbols;
			}
			return this;
		}

		public KnightRiderScannerPartBuilder withSymbolsK2000() {
			return withSymbols(DEFAULT_SYMBOLS);
		}

		public KnightRiderScannerPartBuilder withPresetDots() {
			return withSymbols("·").withEmptyChar('.');
		}

		public KnightRiderScannerPartBuilder withPresetTarget() {
			return withSymbols("┼").withEmptyChar('─');
		}

		public KnightRiderScannerPartBuilder withEmptyChar(char emptyChar) {
			this.emptyChar = emptyChar;
			return this;
		}

		public KnightRiderScannerPartBuilder withRefreshInMilliseconds(Long refreshInMilliseconds) {
			this.refreshInMilliseconds = refreshInMilliseconds;
			return this;
		}

		public KnightRiderScannerPart build() {
			return new KnightRiderScannerPart(this);
		}
	}

	// *************************************************

	private static char[] reverse(char[] chars) {
		final int length = chars.length;
		char[] reversed = new char[length];
		int p = 0;
		for(char c : chars) {
			reversed[length - ++p] = c;
		}
		return reversed;
	}

	private static int assertWidth(int width) {
		if(width <= 0) {
			throw new IllegalArgumentException("Width must be positive: " + width);
		}
		return width;
	}

	// ------------------------------------------

	private static interface Symbol {

		Next next(int position, int width, char[] chars, char[] reversedChars, char empty);

	}

	// ------------------------------------------

	private static record Next(int position, Symbol symbol, String chars) {}

	// ------------------------------------------

	private static class StartLeftToRightSymbol implements Symbol {

		@Override
		public Next next(int position, int width, char[] chars, char[] reversedChars, char empty) {
			if(position == width) {
				return LEFT_2_RIGHT.next(position - 2, width, chars, reversedChars, empty);
			}
			char[] c = new char[width];
			Arrays.fill(c, empty);
			for(int i = 0; i < Math.min(reversedChars.length, position + 1); ++i) {
				c[position - i] = reversedChars[i];
			}
			return new Next(position + 1, this, new String(c));
		}

	}

	// ------------------------------------------

	private static class RightToLeftSymbol implements Symbol {

		@Override
		public Next next(int position, int width, char[] chars, char[] reversedChars, char empty) {
			char[] c = new char[width];
			Arrays.fill(c, empty);
			if(position == 0) {
				System.arraycopy(reversedChars, 0, c, 0, reversedChars.length);
				return new Next(position + 1, RIGHT_2_LEFT, new String(c));
			}
			if(width - position > reversedChars.length) {
				System.arraycopy(reversedChars, 0, c, position, reversedChars.length);
			} else {
				int i = width - (position + reversedChars.length - width) - 1;
				Iterator<Character> it = Arrays.asList(ArrayUtils.toObject(chars)).iterator();
				while(i < width && it.hasNext()) {
					c[i++] = it.next();
				}
				--i;
				while(it.hasNext()) {
					c[--i] = it.next();
				}
			}
			return new Next(position - 1, this, new String(c));
		}

	}

	// ------------------------------------------

	private static class LeftToRightSymbol implements Symbol {

		@Override
		public Next next(int position, int width, char[] chars, char[] reversedChars, char empty) {
			char[] c = new char[width];
			Arrays.fill(c, empty);
			if(position == width) {
				return LEFT_2_RIGHT.next(position - 2, width, chars, reversedChars, empty);
			}
			if(position >= reversedChars.length) {
				System.arraycopy(chars, 0, c, position - chars.length + 1, chars.length);
			} else {
				int i = reversedChars.length - position - 1;
				Iterator<Character> it = Arrays.asList(ArrayUtils.toObject(chars)).iterator();
				while(i >= 0 && it.hasNext()) {
					c[i--] = it.next();
				}
				++i;
				while(it.hasNext()) {
					c[++i] = it.next();
				}
			}
			return new Next(position + 1, this, new String(c));
		}

	}
}
