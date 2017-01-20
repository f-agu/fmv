package org.fagu.fmv.ffmpeg.exception;

/**
 * @author f.agu
 */
public class InvalidDataExceptionKnownAnalyzer extends MovieExceptionKnownAnalyzer {

	/**
	 * 
	 */
	public InvalidDataExceptionKnownAnalyzer() {
		super("Invalid data", ": Invalid data found when processing input");
	}

}
