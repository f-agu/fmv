package org.fagu.fmv.ffmpeg.exception;

/**
 * @author f.agu
 */
public class InvalidDataExceptionKnown extends MovieExceptionKnown {

	/**
	 * 
	 */
	public InvalidDataExceptionKnown() {
		super("Invalid data", ": Invalid data found when processing input");
	}

}
