package org.fagu.fmv.textprogressbar.part;

import java.util.OptionalLong;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class SpinnerPart implements Part {

	private static final char[] CHARS = {'-', '\\', '|', '/'};

	private static final int NB_CHARS = CHARS.length;

	private static final String BLANK = " ";

	private volatile int counter;

	private final OptionalLong refresh;

	/**
	 * 
	 */
	public SpinnerPart() {
		this(1);
	}

	/**
	 * @param numberOfTurnsPerSecond
	 */
	public SpinnerPart(double numberOfTurnsPerSecond) {
		refresh = OptionalLong.of((long)(1000D / (numberOfTurnsPerSecond * NB_CHARS)));
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(org.fagu.fmv.textprogressbar.ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		if(status.isFinished()) {
			return BLANK;
		}
		return Character.toString(CHARS[counter++ % NB_CHARS]);
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getRefreshInMilliseconds()
	 */
	@Override
	public OptionalLong getRefreshInMilliseconds() {
		return refresh;
	}

}
