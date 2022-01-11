package org.fagu.fmv.soft.exec;

import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Watchdog;
import org.fagu.fmv.soft.exec.exception.TimeoutExecuteException;


/**
 * @author Oodrive
 * @author f.agu
 * @created 11 janv. 2022 10:16:11
 */
public class FMVExecuteWatchdog extends ExecuteWatchdog {

	private final Watchdog watchdog;

	private final long timeout;

	private boolean timeoutOccured;

	public FMVExecuteWatchdog(long timeout) {
		super(ExecuteWatchdog.INFINITE_TIMEOUT);
		if(timeout == INFINITE_TIMEOUT) {
			throw new IllegalArgumentException();
		}
		this.timeout = timeout;
		this.watchdog = new Watchdog(timeout);
		this.watchdog.addTimeoutObserver(this);
	}

	@Override
	public synchronized void start(final Process processToMonitor) {
		super.start(processToMonitor);
		watchdog.start();
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

	@Override
	public synchronized void stop() {
		super.stop();
		watchdog.stop();
	}
}
