package org.fagu.fmv.soft.exec.exception;

import java.time.Duration;

import org.apache.commons.exec.ExecuteException;


/**
 * @author Oodrive
 * @author f.agu
 * @created 11 janv. 2022 10:20:20
 */
public class TimeoutExecuteException extends ExecuteException {

	private static final long serialVersionUID = 453371740733460317L;

	private final Duration timeout;

	public TimeoutExecuteException(Duration timeout) {
		super(timeout.toString(), Integer.MAX_VALUE);
		this.timeout = timeout;
	}

	public TimeoutExecuteException(Duration timeout, Throwable cause) {
		super(timeout.toString(), Integer.MAX_VALUE, cause);
		this.timeout = timeout;
	}

	public Duration getTimeout() {
		return timeout;
	}

}
