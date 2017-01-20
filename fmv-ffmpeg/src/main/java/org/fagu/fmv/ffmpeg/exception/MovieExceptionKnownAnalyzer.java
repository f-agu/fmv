package org.fagu.fmv.ffmpeg.exception;

import org.fagu.fmv.soft.exec.exception.SimpleExceptionKnownAnalyzer;


/**
 * @author f.agu
 */
public class MovieExceptionKnownAnalyzer extends SimpleExceptionKnownAnalyzer {

	/**
	 * @param title
	 * @param strToFind
	 */
	public MovieExceptionKnownAnalyzer(String title, String strToFind) {
		super(title, strToFind);
	}

}
