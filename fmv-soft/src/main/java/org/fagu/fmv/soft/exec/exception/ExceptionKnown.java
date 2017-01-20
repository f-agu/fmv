package org.fagu.fmv.soft.exec.exception;

import java.util.Objects;


/**
 * @author fagu
 */
public class ExceptionKnown {

	private final String message;

	/**
	 * @param message
	 */
	public ExceptionKnown(String message) {
		this.message = Objects.requireNonNull(message);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return message;
	}
}
