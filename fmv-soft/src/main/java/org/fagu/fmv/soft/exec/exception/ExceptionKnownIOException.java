package org.fagu.fmv.soft.exec.exception;

import java.io.IOException;
import java.util.Objects;


/**
 * @author Oodrive
 * @author f.agu
 * @created 23 janv. 2017 11:37:15
 */
public class ExceptionKnownIOException extends IOException {

	private static final long serialVersionUID = - 4821570989179572851L;

	private final ExceptionKnown exceptionKnown;

	/**
	 * @param exceptionKnown
	 */
	public ExceptionKnownIOException(ExceptionKnown exceptionKnown) {
		this.exceptionKnown = Objects.requireNonNull(exceptionKnown);
	}

	/**
	 * @return
	 */
	public ExceptionKnown getExceptionKnown() {
		return exceptionKnown;
	}

}
