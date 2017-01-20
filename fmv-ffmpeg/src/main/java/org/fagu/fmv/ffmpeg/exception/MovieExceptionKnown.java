package org.fagu.fmv.ffmpeg.exception;

import org.fagu.fmv.soft.exec.exception.FindStringSPIExceptionKnown;


/**
 * @author f.agu
 */
public class MovieExceptionKnown extends FindStringSPIExceptionKnown {

	/**
	 * @param title
	 * @param strToFind
	 */
	public MovieExceptionKnown(String title, String strToFind) {
		super(title, strToFind);
	}

}
