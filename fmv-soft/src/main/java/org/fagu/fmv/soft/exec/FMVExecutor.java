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
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.ExecutorService;
import java.util.function.IntConsumer;
import java.util.function.UnaryOperator;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.utils.Proxifier;


/**
 * @author f.agu
 */
public class FMVExecutor extends DefaultExecutor {

	private Long timeOutMilliSeconds;

	private ExecuteWatchdog executeWatchdog;

	private final Proxifier<FMVExecListener> listenerProxifier;

	private final FMVExecListener proxyFMVExecListener;

	private UnaryOperator<Process> processOperator = getDefaultProcessOperator();

	// --------------

	/**
	 * @param workingFolder
	 * @param outAndErrReadLine
	 * @param charset
	 * @return
	 */
	public static FMVExecutor create(File workingFolder, ReadLine outAndErrReadLine, Charset charset) {
		return create(workingFolder, new ReadLinePumpStreamHandler(outAndErrReadLine, charset));
	}

	/**
	 * @param workingFolder
	 * @param outReadLine
	 * @param errReadLine
	 * @param charset
	 * @return
	 */
	public static FMVExecutor create(File workingFolder, ReadLine outReadLine, ReadLine errReadLine, Charset charset) {
		return create(workingFolder, new ReadLinePumpStreamHandler(outReadLine, errReadLine, charset));
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
		addListener(ExecStats.getInstance().getExecListener());
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
		if(timeOutMilliSeconds >= 0 && timeOutMilliSeconds < 10) {
			throw new IllegalArgumentException("Incredible timeout: " + timeOutMilliSeconds + "ms");
		}
		this.timeOutMilliSeconds = Math.max(timeOutMilliSeconds, org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT);
		executeWatchdog = new ExecuteWatchdog(timeOutMilliSeconds);
		setWatchdog(executeWatchdog);
	}

	/**
	 * @return
	 */
	public OptionalLong getTimeOut() {
		return timeOutMilliSeconds == null ? OptionalLong.empty() : OptionalLong.of(timeOutMilliSeconds);
	}

	/**
	 * 
	 */
	public void removeTimeOut() {
		timeOutMilliSeconds = null;
		executeWatchdog = null;
		setWatchdog(null);
	}

	/**
	 * @param processOperator
	 */
	public void setProcessOperator(UnaryOperator<Process> processOperator) {
		this.processOperator = processOperator;
	}

	/**
	 * 
	 */
	public void removeProcessOperator() {
		setProcessOperator(null);
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
		proxyFMVExecListener.eventPreExecute(this, command, environment, null);
		try {
			int exitValue = super.execute(command, environment);
			proxyFMVExecListener.eventPostExecute(this, command, environment, null);
			return exitValue;
		} catch(IOException e) {
			proxyFMVExecListener.eventFailed(this, command, environment, null, e);
			throw e;
		}
	}

	/**
	 * @see org.apache.commons.exec.DefaultExecutor#execute(org.apache.commons.exec.CommandLine, java.util.Map,
	 *      org.apache.commons.exec.ExecuteResultHandler)
	 */
	@Override
	public void execute(CommandLine command, Map<String, String> environment, ExecuteResultHandler handler) throws ExecuteException, IOException {
		proxyFMVExecListener.eventPreExecute(this, command, environment, handler);
		try {
			try {
				super.execute(command, environment, handler);
				proxyFMVExecListener.eventPostExecute(this, command, environment, handler);
			} catch(ExecuteException e) {
				throw new ExecuteException(command.toString(), e.getExitValue(), e);
			}
		} catch(IOException e) {
			proxyFMVExecListener.eventFailed(this, command, environment, handler, e);
			throw e;
		}
	}

	/**
	 * @param command
	 * @param executorService
	 * @return
	 */
	public FMVFuture<Integer> executeAsynchronous(CommandLine command, ExecutorService executorService) {
		return executeAsynchronous(command, executorService, null, null, null);
	}

	/**
	 * @param command
	 * @param executorService
	 * @param before
	 * @param after
	 * @param ioExceptionConsumer
	 * @return
	 */
	public FMVFuture<Integer> executeAsynchronous(CommandLine command, ExecutorService executorService, Runnable before, IntConsumer after,
			IOExceptionConsumer ioExceptionConsumer) {
		ExecuteStreamHandler streamHandler = getStreamHandler();
		WritablePumpStreamHandler wpsh = streamHandler instanceof WritablePumpStreamHandler ? (WritablePumpStreamHandler)streamHandler : null;
		return new FMVFuture<>(executorService.submit(() -> {
			if(before != null) {
				before.run();
			}
			try {
				int exitValue = execute(command);
				if(after != null) {
					after.accept(exitValue);
				}
				return exitValue;
			} catch(IOException e) {
				if(ioExceptionConsumer == null) {
					throw e;
				}
				ioExceptionConsumer.accept(e);
				if(e instanceof ExecuteException) {
					return ((ExecuteException)e).getExitValue();
				}
				return - 1; // TODO
			}
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

	// -------------------------------------------

	/**
	 * @author Oodrive
	 * @author f.agu
	 * @created 21 avr. 2017 10:45:12
	 */
	public interface IOExceptionConsumer {

		/**
		 * @param exception
		 * @throws IOException
		 */
		void accept(IOException exception) throws IOException;
	}

	// ****************************************

	/**
	 * @see org.apache.commons.exec.DefaultExecutor#launch(org.apache.commons.exec.CommandLine, java.util.Map,
	 *      java.io.File)
	 */
	@Override
	protected Process launch(CommandLine command, Map<String, String> env, File dir) throws IOException {
		Process superProcess = super.launch(command, env, dir);
		return processOperator != null ? processOperator.apply(superProcess) : superProcess;
	}

	/**
	 * @return
	 */
	private static UnaryOperator<Process> getDefaultProcessOperator() {
		if( ! SystemUtils.IS_OS_UNIX) {
			return null;
		}
		return process -> new WrapProcess(process) {

			@Override
			public OutputStream getOutputStream() {
				return new FilterOutputStream(super.getOutputStream()) {

					@Override
					public void flush() throws IOException {
						try {
							super.flush();
						} catch(Exception e) {
							// ignore
						}
					}

					@Override
					public void close() throws IOException {
						try {
							super.close();
						} catch(Exception e) {
							// ignore
						}
					}
				};
			}
		};
	}
}
