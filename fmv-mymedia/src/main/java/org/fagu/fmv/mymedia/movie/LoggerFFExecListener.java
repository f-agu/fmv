package org.fagu.fmv.mymedia.movie;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteResultHandler;
import org.fagu.fmv.ffmpeg.executor.FFExecFallback;
import org.fagu.fmv.ffmpeg.executor.FFExecListener;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author f.agu
 * @created 3 juil. 2018 22:54:00
 */
public class LoggerFFExecListener implements FFExecListener {

	private final Logger logger;

	public LoggerFFExecListener(Logger logger) {
		this.logger = Objects.requireNonNull(logger);
	}

	@Override
	public void eventPreExecute(FMVExecutor fmvExecutor, CommandLine command,
			@SuppressWarnings("rawtypes") Map environment, ExecuteResultHandler handler) {
		logger.log("Exec: " + CommandLineUtils.toLine(command));
	}

	@Override
	public void eventPreExecFallbacks(CommandLine command, Collection<FFExecFallback> fallbacks) {
		logger.log("Exec fallback " + fallbacks + ": " + CommandLineUtils.toLine(command));
	}

	@Override
	public void eventFallbackNotFound(CommandLine command, List<String> outputs) {
		logger.log("Fallback not found: " + CommandLineUtils.toLine(command));
		outputs.forEach(logger::log);
	}
}
