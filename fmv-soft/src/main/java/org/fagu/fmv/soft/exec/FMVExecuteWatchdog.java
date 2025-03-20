package org.fagu.fmv.soft.exec;

import java.time.Duration;

import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Watchdog;
import org.fagu.fmv.soft.exec.exception.TimeoutExecuteException;


/**
 * @author Oodrive
 * @author f.agu
 * @created 11 janv. 2022 10:16:11
 */
public class FMVExecuteWatchdog extends ExecuteWatchdog {

	private final Duration timeout;

	private boolean timeoutOccured;

	public FMVExecuteWatchdog(Duration timeout) {
		super(timeout.toMillis());
		if(timeout == INFINITE_TIMEOUT_DURATION) {
			throw new IllegalArgumentException();
		}
		this.timeout = timeout;
	}

	@Override
	public synchronized void timeoutOccured(Watchdog w) {
		timeoutOccured = w != null;
		super.timeoutOccured(w);
	}

	@Override
	public synchronized void checkException() throws Exception {
		super.checkException();
		if(timeoutOccured && killedProcess()) {
			throw new TimeoutExecuteException(timeout);
		}
	}

}
