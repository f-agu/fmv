package org.fagu.fmv.textprogressbar.part;

/**
 * @author fagu
 */
public class SpinnerPart extends ChainedCharPart {

	private static final char[] CHARS = {'-', '\\', '|', '/'};

	public SpinnerPart() {
		super(CHARS, 1);
	}

	public SpinnerPart(double numberOfTurnsPerSecond) {
		super(CHARS, numberOfTurnsPerSecond);
	}

}
