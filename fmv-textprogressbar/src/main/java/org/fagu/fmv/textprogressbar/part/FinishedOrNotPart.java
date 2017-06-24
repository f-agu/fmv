package org.fagu.fmv.textprogressbar.part;

import java.util.Objects;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class FinishedOrNotPart implements Part {

	private final Part notFinishedPart;

	private final Part finishedPart;

	/**
	 * @param notFinishedPart
	 * @param finishedPart
	 */
	public FinishedOrNotPart(Part notFinishedPart, Part finishedPart) {
		this.notFinishedPart = Objects.requireNonNull(notFinishedPart);
		this.finishedPart = Objects.requireNonNull(finishedPart);
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		return status.isFinished() ? finishedPart.getWith(status) : notFinishedPart.getWith(status);
	}

}
