package org.fagu.fmv.textprogressbar.part;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class SpendTimePart implements Part {

	private final long startTime = System.currentTimeMillis();

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		StringBuilder buf = new StringBuilder();
		long endTime = System.currentTimeMillis();
		long diffSec = (endTime - startTime) / 1000;
		int seconds = (int)(diffSec % 60L);
		int minutes = (int)(diffSec / 60L);
		buf.append(StringUtils.leftPad(Integer.toString(minutes), 4)).append(':');
		buf.append(StringUtils.leftPad(Integer.toString(seconds), 2, '0'));
		return buf.toString();
	}

}
