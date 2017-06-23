package org.fagu.fmv.textprogressbar;

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
}
