package org.fagu.fmv.mymedia.movie;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
