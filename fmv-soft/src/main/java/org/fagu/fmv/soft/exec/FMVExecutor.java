package org.fagu.fmv.soft.exec;

/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 fagu
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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.fagu.fmv.soft.utils.Proxifier;


/**
 * @author f.agu
 */
public class FMVExecutor extends DefaultExecutor {

	private static final long DEFAULT_TIME_OUT = 1L * 60L * 60L * 1000L; // 1 hour

	private long timeOutMilliSeconds = DEFAULT_TIME_OUT;

	private ExecuteWatchdog executeWatchdog;

	private final Proxifier<FMVExecListener> listenerProxifier;

	private final FMVExecListener proxyFMVExecListener;

	// --------------

	/**
	 * @param workingFolder
	 * @param outAndErrReadLine
	 * @return
	 */
	public static FMVExecutor create(File workingFolder, ReadLine outAndErrReadLine) {
		return create(workingFolder, new ReadLinePumpStreamHandler(outAndErrReadLine));
	}

	/**
	 * @param workingFolder
	 * @param outReadLine
	 * @param errReadLine
	 * @return
	 */
	public static FMVExecutor create(File workingFolder, ReadLine outReadLine, ReadLine errReadLine) {
		return create(workingFolder, new ReadLinePumpStreamHandler(outReadLine, errReadLine));
	}

	/**
	 * @param workingFolder
	 * @param executeStreamHandler
	 * @return
	 */
	public static FMVExecutor create(File workingFolder, ExecuteStreamHandler executeStreamHandler) {
		FMVExecutor executor = new FMVExecutor();
		if(workingFolder != null) {
			executor.setWorkingDirectory(workingFolder);
		}
		if(executeStreamHandler != null) {
			executor.setStreamHandler(executeStreamHandler);
		} else {
			executor.setStreamHandler(new WritablePumpStreamHandler());
		}
		return executor;
	}

	// --------------

	/**
	 * 
	 */
	public FMVExecutor() {
		listenerProxifier = new Proxifier<>(FMVExecListener.class);
		proxyFMVExecListener = listenerProxifier.proxify();

		addProcessDestroyer(new ShutdownHookProcessDestroyer());
	}

	/**
	 * @param fmvExecListener
	 */
	public void addListener(FMVExecListener fmvExecListener) {
		listenerProxifier.add(fmvExecListener);
	}

	/**
	 * @param timeOutMilliSeconds
	 */
	public void setTimeOut(long timeOutMilliSeconds) {
		this.timeOutMilliSeconds = Math.max(timeOutMilliSeconds, org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT);
		executeWatchdog = new ExecuteWatchdog(timeOutMilliSeconds);
		setWatchdog(executeWatchdog);
	}

	/**
	 * @return
	 */
	public long getTimeOut() {
		return timeOutMilliSeconds;
	}

	/**
	 * @param processDestroyer
	 */
	public void addProcessDestroyer(ProcessDestroyer processDestroyer) {
		ProcessDestroyer pd = getProcessDestroyer();
		AggregateProcessDestroyer aggregateProcessDestroyer = null;
		if(pd instanceof AggregateProcessDestroyer) {
			aggregateProcessDestroyer = (AggregateProcessDestroyer)pd;
		} else {
			aggregateProcessDestroyer = new AggregateProcessDestroyer();
			aggregateProcessDestroyer.add(pd);
		}
		aggregateProcessDestroyer.add(processDestroyer);
	}

	/**
	 * @see org.apache.commons.exec.DefaultExecutor#execute(org.apache.commons.exec.CommandLine, java.util.Map)
	 */
	@Override
	public int execute(CommandLine command, Map<String, String> environment) throws ExecuteException, IOException {
		try {
			proxyFMVExecListener.eventPreExecute(this, command, environment, null);
			int exitValue = super.execute(command, environment);
			proxyFMVExecListener.eventPostExecute(this, command, environment, null);
			return exitValue;
		} catch(ExecuteException e) {
			throw new ExecuteException(command.toString(), e.getExitValue(), e);
		}
	}

	/**
	 * @see org.apache.commons.exec.DefaultExecutor#execute(org.apache.commons.exec.CommandLine, java.util.Map,
	 *      org.apache.commons.exec.ExecuteResultHandler)
	 */
	@Override
	public void execute(CommandLine command, Map<String, String> environment, ExecuteResultHandler handler) throws ExecuteException, IOException {
		try {
			proxyFMVExecListener.eventPreExecute(this, command, environment, handler);
			super.execute(command, environment, handler);
			proxyFMVExecListener.eventPostExecute(this, command, environment, handler);
		} catch(ExecuteException e) {
			throw new ExecuteException(command.toString(), e.getExitValue(), e);
		}
	}

	/**
	 * @param command
	 * @param executorService
	 * @return
	 * @throws ExecuteException
	 * @throws IOException
	 */
	public FMVFuture<Integer> executeAsynchronous(CommandLine command, ExecutorService executorService) throws ExecuteException, IOException {
		return executeAsynchronous(command, executorService, null, null);
	}

	/**
	 * @param command
	 * @param executorService
	 * @param before
	 * @param after
	 * @return
	 * @throws ExecuteException
	 * @throws IOException
	 */
	public FMVFuture<Integer> executeAsynchronous(CommandLine command, ExecutorService executorService, Runnable before, Runnable after)
			throws ExecuteException, IOException {
		ExecuteStreamHandler streamHandler = getStreamHandler();
		WritablePumpStreamHandler wpsh = streamHandler instanceof WritablePumpStreamHandler ? (WritablePumpStreamHandler)streamHandler : null;
		return new FMVFuture<Integer>(executorService.submit(() -> {
			if(before != null) {
				before.run();
			}
			int exitValue = execute(command);
			if(after != null) {
				after.run();
			}
			return exitValue;
		}), wpsh);
	}

	/**
	 * 
	 */
	public void destroy() {
		if(executeWatchdog != null && ! executeWatchdog.killedProcess()) {
			executeWatchdog.destroyProcess();
			executeWatchdog = null;
		}
	}

}
