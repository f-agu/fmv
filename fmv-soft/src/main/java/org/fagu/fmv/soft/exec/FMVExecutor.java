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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.fagu.fmv.soft.AroundExecute;
import org.fagu.fmv.soft.AroundExecuteSupplier;
import org.fagu.fmv.soft.ExecuteDelegate;
import org.fagu.fmv.soft.ExecuteDelegateRepository;
import org.fagu.fmv.soft.utils.Proxifier;
import org.fagu.fmv.utils.order.OrderComparator;


/**
 * @author f.agu
 */
public class FMVExecutor extends DefaultExecutor {

	private static AroundExecuteSupplier globalAroundExecuteSupplier;

	// ---------------------------------------------

	public static class FMVExecutorBuilder {

		private final File workingFolder;

		private ReadLine outReadLine;

		private ReadLine errReadLine;

		private Charset charset;

		private LookReader lookReader;

		private ExecuteStreamHandler executeStreamHandler;

		private boolean enabledDefaultInvalidExitValue;

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

		public FMVExecutorBuilder enableDefaultInvalidExitValue(boolean enabledDefaultInvalidExitValue) {
			this.enabledDefaultInvalidExitValue = enabledDefaultInvalidExitValue;
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

	private final boolean enabledDefaultInvalidExitValue;

	private List<ProcessOperator> processOperators = new ArrayList<>();

	private AroundExecuteSupplier aroundExecuteSupplier;

	// --------------

	private FMVExecutor(FMVExecutorBuilder builder) {
		if(builder.workingFolder != null) {
			setWorkingDirectory(builder.workingFolder);
		}
		setStreamHandler(builder.executeStreamHandler);

		listenerProxifier = new Proxifier<>(FMVExecListener.class);
		proxyFMVExecListener = listenerProxifier.proxify();

		this.enabledDefaultInvalidExitValue = builder.enabledDefaultInvalidExitValue;

		addProcessDestroyer(new ShutdownHookProcessDestroyer());
		addListener(ExecStats.getInstance().getExecListener());

		addProcessOperator(new IgnoreNullOutputStreamProcessOperator());
	}

	public static FMVExecutorBuilder with(File workingFolder) {
		return new FMVExecutorBuilder(workingFolder);
	}

	public void addListener(FMVExecListener fmvExecListener) {
		listenerProxifier.add(fmvExecListener);
	}

	public void setTimeOut(long timeOutMilliSeconds) {
		if(timeOutMilliSeconds >= 0 && timeOutMilliSeconds < 10) {
			throw new IllegalArgumentException("Incredible timeout: " + timeOutMilliSeconds + "ms");
		}
		this.timeOutMilliSeconds = Math.max(timeOutMilliSeconds, org.apache.commons.exec.ExecuteWatchdog.INFINITE_TIMEOUT);
		executeWatchdog = new FMVExecuteWatchdog(timeOutMilliSeconds);
		setWatchdog(executeWatchdog);
	}

	public OptionalLong getTimeOut() {
		return timeOutMilliSeconds == null ? OptionalLong.empty() : OptionalLong.of(timeOutMilliSeconds);
	}

	public void removeTimeOut() {
		timeOutMilliSeconds = null;
		executeWatchdog = null;
		setWatchdog(null);
	}

	public void addProcessOperator(ProcessOperator processOperator) {
		processOperators.add(processOperator);
	}

	public void addProcessOperators(Collection<ProcessOperator> processOperators) {
		this.processOperators.addAll(processOperators);
	}

	public AroundExecuteSupplier getAroundExecuteSupplier() {
		return aroundExecuteSupplier;
	}

	public void setAroundExecuteSupplier(AroundExecuteSupplier aroundExecuteSupplier) {
		this.aroundExecuteSupplier = aroundExecuteSupplier;
	}

	public void clearProcessOperators() {
		processOperators.clear();
	}

	public void addProcessDestroyer(ProcessDestroyer processDestroyer) {
		if(processDestroyer == null) {
			return;
		}
		ProcessDestroyer pd = getProcessDestroyer();
		if(pd == null) {
			setProcessDestroyer(processDestroyer);
			return;
		}
		AggregateProcessDestroyer aggregateProcessDestroyer = null;
		if(pd instanceof AggregateProcessDestroyer) {
			aggregateProcessDestroyer = (AggregateProcessDestroyer)pd;
		} else {
			aggregateProcessDestroyer = new AggregateProcessDestroyer();
			aggregateProcessDestroyer.add(pd);
		}
		aggregateProcessDestroyer.add(processDestroyer);
	}

	@Override
	public int execute(CommandLine command, Map<String, String> environment) throws IOException {
		Map<String, String> envMap = copyEnvironment(environment);
		try (AroundExecute aroundExecute = getAroundExecute(command, envMap)) {
			envMap = environmentNotEmpty(envMap);
			proxyFMVExecListener.eventPreExecute(this, command, envMap, null);
			int exitValue = super.execute(command, envMap);
			proxyFMVExecListener.eventPostExecute(this, command, envMap, null);
			return exitValue;
		} catch(IOException e) {
			proxyFMVExecListener.eventFailed(this, command, envMap, null, e);
			throw e;
		}
	}

	@Override
	public void execute(CommandLine command, Map<String, String> environment, ExecuteResultHandler handler) throws IOException {
		Map<String, String> envMap = copyEnvironment(environment);
		try {
			try (AroundExecute aroundExecute = getAroundExecute(command, envMap)) {
				envMap = environmentNotEmpty(envMap);
				proxyFMVExecListener.eventPreExecute(this, command, envMap, handler);
				super.execute(command, envMap, handler);
				proxyFMVExecListener.eventPostExecute(this, command, envMap, handler);
			} catch(ExecuteException e) {
				throw new ExecuteException(command.toString(), e.getExitValue(), e);
			}
		} catch(IOException e) {
			proxyFMVExecListener.eventFailed(this, command, envMap, handler, e);
			throw e;
		}
	}

	public FMVFuture<Integer> executeAsynchronous(CommandLine command, ExecutorService executorService) {
		return executeAsynchronous(ExecuteDelegateRepository.get(), command, executorService, null, null, null);
	}

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

	public void destroy() {
		if(executeWatchdog != null && ! executeWatchdog.killedProcess()) {
			executeWatchdog.destroyProcess();
			executeWatchdog = null;
		}
	}

	@Override
	public boolean isFailure(int exitValue) {
		if( ! enabledDefaultInvalidExitValue && Executor.INVALID_EXITVALUE == exitValue) {
			return false;
		}
		return super.isFailure(exitValue);
	}

	public static AroundExecuteSupplier getGlobalAroundExecuteSupplier() {
		return globalAroundExecuteSupplier;
	}

	public static void setGlobalAroundExecuteSupplier(AroundExecuteSupplier globalAroundExecuteSupplier) {
		FMVExecutor.globalAroundExecuteSupplier = globalAroundExecuteSupplier;
	}

	// -------------------------------------------

	/**
	 * @author f.agu
	 * @created 21 avr. 2017 10:45:12
	 */
	public interface IOExceptionConsumer {

		void accept(IOException exception) throws IOException;
	}

	// ****************************************

	@Override
	protected Process launch(CommandLine command, Map<String, String> env, File dir) throws IOException {
		Process process = super.launch(command, env, dir);
		OrderComparator.sort(processOperators);
		Set<Class<? extends ProcessOperator>> set = new HashSet<>(2);
		List<ProcessOperator> distinctList = processOperators.stream()
				.filter(po -> set.add(po.getClass()))
				.collect(Collectors.toList());
		for(ProcessOperator processOperator : distinctList) {
			process = Objects.requireNonNull(processOperator.operate(process), "Return null ProcessOperator on " + processOperator.toString());
		}
		return process;
	}

	// ****************************************

	private AroundExecute getAroundExecute(CommandLine command, Map<String, String> environment) throws IOException {
		AroundExecute aroundExecute = null;
		if(aroundExecuteSupplier != null) {
			aroundExecute = aroundExecuteSupplier.get(command, environment);
		} else if(globalAroundExecuteSupplier != null) {
			aroundExecute = globalAroundExecuteSupplier.get(command, environment);
		}
		if(aroundExecute == null) {
			aroundExecute = AroundExecute.nothing();
		} else {
			aroundExecute.initialize(this, command, environment);
		}
		return aroundExecute;
	}

	private Map<String, String> copyEnvironment(Map<String, String> environment) {
		return environment != null ? new HashMap<>(environment) : new HashMap<>(System.getenv());
	}

	private Map<String, String> environmentNotEmpty(Map<String, String> environment) {
		return environment == null || environment.isEmpty() ? null : environment;
	}

}
