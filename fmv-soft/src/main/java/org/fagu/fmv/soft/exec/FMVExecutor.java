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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.ExecutorService;
import java.util.function.IntConsumer;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.fagu.fmv.soft.BasicExecuteDelegate;
import org.fagu.fmv.soft.ExecuteDelegate;
import org.fagu.fmv.soft.utils.Proxifier;
import org.fagu.fmv.utils.order.OrderComparator;


/**
 * @author f.agu
 */
public class FMVExecutor extends DefaultExecutor {

	// ---------------------------------------------

	public static class FMVExecutorBuilder {

		private final File workingFolder;

		private ReadLine outReadLine;

		private ReadLine errReadLine;

		private Charset charset;

		private LookReader lookReader;

		private ExecuteStreamHandler executeStreamHandler;

		private FMVExecutorBuilder(File workingFolder) {
			this.workingFolder = Objects.requireNonNull(workingFolder);
		}

		public FMVExecutorBuilder out(ReadLine outReadLine) {
			this.outReadLine = outReadLine;
			return this;
		}

		public FMVExecutorBuilder err(ReadLine errReadLine) {
			this.errReadLine = errReadLine;
			return this;
		}

		public FMVExecutorBuilder outAndErr(ReadLine outAndErrReadLine) {
			return out(outAndErrReadLine).err(outAndErrReadLine);
		}

		public FMVExecutorBuilder charset(Charset charset) {
			this.charset = charset;
			return this;
		}

		public FMVExecutorBuilder lookReader(LookReader lookReader) {
			this.lookReader = lookReader;
			return this;
		}

		public FMVExecutorBuilder executeStreamHandler(ExecuteStreamHandler executeStreamHandler) {
			this.executeStreamHandler = executeStreamHandler;
			return this;
		}

		public FMVExecutor build() {
			if(executeStreamHandler == null && (outReadLine != null || errReadLine != null)) {
				executeStreamHandler = new ReadLinePumpStreamHandler(outReadLine, errReadLine, charset, lookReader);
			}
			if(executeStreamHandler == null) {
				executeStreamHandler = new WritablePumpStreamHandler();
			}
			if(lookReader instanceof WritablePumpStreamNeed && executeStreamHandler instanceof WritablePumpStreamHandler) {
				((WritablePumpStreamNeed)lookReader).apply((WritablePumpStreamHandler)executeStreamHandler);
			}
			return new FMVExecutor(this);
		}
	}

	// ---------------------------------------------

	private Long timeOutMilliSeconds;

	private ExecuteWatchdog executeWatchdog;

	private final Proxifier<FMVExecListener> listenerProxifier;

	private final FMVExecListener proxyFMVExecListener;

	private List<ProcessOperator> processOperators = new ArrayList<>();

	// --------------

	/**
	 * @param builder
	 */
	private FMVExecutor(FMVExecutorBuilder builder) {
		if(builder.workingFolder != null) {
			setWorkingDirectory(builder.workingFolder);
		}
		setStreamHandler(builder.executeStreamHandler);

		listenerProxifier = new Proxifier<>(FMVExecListener.class);
		proxyFMVExecListener = listenerProxifier.proxify();

		addProcessDestroyer(new ShutdownHookProcessDestroyer());
		addListener(ExecStats.getInstance().getExecListener());

		addProcessOperator(new IgnoreNullOutputStreamProcessOperator());
	}

	/**
	 * @param workingFolder
	 * @return
	 */
	public static FMVExecutorBuilder with(File workingFolder) {
		return new FMVExecutorBuilder(workingFolder);
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
	public void addProcessOperator(ProcessOperator processOperator) {
		processOperators.add(processOperator);
	}

	/**
	 * @param processOperators
	 */
	public void addProcessOperators(Collection<ProcessOperator> processOperators) {
		this.processOperators.addAll(processOperators);
	}

	/**
	 * 
	 */
	public void clearProcessOperators() {
		processOperators.clear();
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
	public int execute(CommandLine command, Map<String, String> environment) throws IOException {
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
	public void execute(CommandLine command, Map<String, String> environment, ExecuteResultHandler handler) throws IOException {
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
		return executeAsynchronous(BasicExecuteDelegate.INSTANCE, command, executorService, null, null, null);
	}

	/**
	 * @param executeDelegate
	 * @param command
	 * @param executorService
	 * @param before
	 * @param after
	 * @param ioExceptionConsumer
	 * @return
	 */
	public FMVFuture<Integer> executeAsynchronous(ExecuteDelegate executeDelegate, CommandLine command, ExecutorService executorService,
			Runnable before, IntConsumer after,
			IOExceptionConsumer ioExceptionConsumer) {
		ExecuteStreamHandler streamHandler = getStreamHandler();
		WritablePumpStreamHandler wpsh = streamHandler instanceof WritablePumpStreamHandler ? (WritablePumpStreamHandler)streamHandler : null;
		return new FMVFuture<>(executorService.submit(() -> {
			if(before != null) {
				before.run();
			}
			try {
				int exitValue = executeDelegate.execute(this, command);
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
		}), wpsh.getProcessInputStream());
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
		Process process = super.launch(command, env, dir);
		OrderComparator.sort(processOperators);
		for(ProcessOperator processOperator : processOperators) {
			process = Objects.requireNonNull(processOperator.operate(process), "Return null ProcessOperator on " + processOperator.toString());
		}
		return process;
	}

}
