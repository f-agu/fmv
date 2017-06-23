package org.fagu.fmv.textprogressbar.part;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class SpendTimeThenPercentPart implements Part {

	private final SpendTimePart spendTimePart = new SpendTimePart();

	private final PercentPart percentPart = new PercentPart();

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		return status.isFinished() ? spendTimePart.getWith(status) : percentPart.getWith(status);
	}

}
