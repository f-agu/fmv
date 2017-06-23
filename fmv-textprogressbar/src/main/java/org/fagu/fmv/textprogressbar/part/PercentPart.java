package org.fagu.fmv.textprogressbar.part;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class PercentPart implements Part {

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		StringBuilder buf = new StringBuilder();
		buf.append(StringUtils.leftPad(Integer.toString(status.getPercent()), 2)).append("%");
		return buf.toString();
	}

}
