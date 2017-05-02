package org.fagu.fmv.soft;

import org.fagu.fmv.soft.exec.exception.FMVExecuteException;

/**
 * @author f.agu
 */
public interface ExecListener {

	/**
	 * @param cmdLineStr
	 */
	default void eventPrepare(String cmdLineStr) {}

	/**
	 * @param cmdLineStr
	 */
	default void eventExecuting(String cmdLineStr) {}

	/**
	 * @param cmdLineStr
	 * @param exitValue
	 * @param timeMilleseconds
	 */
	default void eventExecuted(String cmdLineStr, int exitValue, long timeMilleseconds) {}

	/**
	 * @param fmvExecuteException
	 */
	default void eventException(FMVExecuteException fmvExecuteException) {}
}