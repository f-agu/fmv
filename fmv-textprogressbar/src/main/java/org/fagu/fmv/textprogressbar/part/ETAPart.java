package org.fagu.fmv.textprogressbar.part;

import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class ETAPart implements Part {

	private final Function<ProgressStatus, Integer> etaInSecondsSupplier;

	/**
	 * @param etaInSecondsSupplier
	 */
	public ETAPart(Function<ProgressStatus, Integer> etaInSecondsSupplier) {
		this.etaInSecondsSupplier = Objects.requireNonNull(etaInSecondsSupplier);
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		Integer etaInSeconds = etaInSecondsSupplier.apply(status);
		if(etaInSeconds != null && etaInSeconds > 0) {
			int seconds = etaInSeconds;
			StringBuilder buf = new StringBuilder();
			buf.append(StringUtils.leftPad(Integer.toString(seconds / 60), 2, '0')).append(':');
			buf.append(StringUtils.leftPad(Integer.toString(seconds % 60), 2, '0')).append(" ETA");
			return buf.toString();
		}
		return StringUtils.EMPTY;
	}

}
