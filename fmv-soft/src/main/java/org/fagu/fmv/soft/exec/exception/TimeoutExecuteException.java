package org.fagu.fmv.soft.exec.exception;

import org.apache.commons.exec.ExecuteException;


/**
 * @author Oodrive
 * @author f.agu
 * @created 11 janv. 2022 10:20:20
 */
public class TimeoutExecuteException extends ExecuteException {

	private static final long serialVersionUID = 453371740733460317L;

	private final long timeout;

	public TimeoutExecuteException(long timeout) {
		super(timeout + "ms", Integer.MAX_VALUE);
		this.timeout = timeout;
	}

	public TimeoutExecuteException(long timeout, Throwable cause) {
		super(timeout + "ms", Integer.MAX_VALUE, cause);
		this.timeout = timeout;
	}

	public long getTimeout() {
		return timeout;
	}

}
