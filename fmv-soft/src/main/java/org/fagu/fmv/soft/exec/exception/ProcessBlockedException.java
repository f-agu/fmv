package org.fagu.fmv.soft.exec.exception;

import java.io.IOException;


/**
 * @author fagu
 */
public class ProcessBlockedException extends IOException {

	private static final long serialVersionUID = 5480038730803650064L;

	/**
	 * 
	 */
	public ProcessBlockedException() {}

	/**
	 * @param message
	 */
	public ProcessBlockedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ProcessBlockedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProcessBlockedException(String message, Throwable cause) {
		super(message, cause);
	}

}
