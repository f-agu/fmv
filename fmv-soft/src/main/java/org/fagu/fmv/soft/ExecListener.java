package org.fagu.fmv.soft;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;


/**
 * @author f.agu
 */
public interface ExecListener {

	default void eventPrepare(CommandLine commandLine) {}

	default void eventExecuting(CommandLine commandLine) {}

	default void eventExecuted(CommandLine commandLine, int exitValue, long timeMilleseconds) {}

	default void eventException(FMVExecuteException fmvExecuteException) {}
}
