package org.fagu.fmv.soft.find;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteResultHandler;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.FMVExecListener;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Oodrive
 * @author f.agu
 * @created 14 déc. 2022 11:42:10
 */
public class LoggerDebugCmdLineFMVExecListener implements FMVExecListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerDebugCmdLineFMVExecListener.class);

	@Override
	public void eventPreExecute(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
			ExecuteResultHandler handler) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {}, env: {}", CommandLineUtils.toLine(command), environment);
		}
	}

	@Override
	public void eventPostExecute(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
			ExecuteResultHandler handler) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executed {}, env: {}", CommandLineUtils.toLine(command), environment);
		}
	}

	@Override
	public void eventFailed(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment, ExecuteResultHandler handler,
			IOException ioe) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Execute failed {}, env: {}", CommandLineUtils.toLine(command), environment, ioe);
		}
	}

}
