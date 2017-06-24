package org.fagu.fmv.textprogressbar;

import java.util.OptionalLong;


/**
 * @author fagu
 */
@FunctionalInterface
public interface Part {

	/**
	 * @param status
	 * @return
	 */
	String getWith(ProgressStatus status);

	/**
	 * @return
	 */
	default OptionalLong getRefreshInMilliseconds() {
		return OptionalLong.empty();
	}
}
