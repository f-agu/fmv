package org.fagu.fmv.textprogressbar.part;

import java.util.Locale;
import java.util.Objects;
import java.util.function.LongSupplier;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;
import org.fagu.fmv.utils.ByteSize;


/**
 * @author fagu
 */
public class ByteSizePart implements Part {

	private final String totalSize;

	private final LongSupplier currentSize;

	private final Locale locale;

	/**
	 * @param currentSize
	 * @param totalSize
	 */
	public ByteSizePart(LongSupplier currentSize, long totalSize, Locale locale) {
		this.currentSize = Objects.requireNonNull(currentSize);
		this.locale = locale;
		this.totalSize = ByteSize.formatSize(totalSize, locale);
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		StringBuilder buf = new StringBuilder();
		buf.append(ByteSize.formatSize(currentSize.getAsLong(), locale)).append(" / ").append(totalSize);
		return buf.toString();
	}

}
