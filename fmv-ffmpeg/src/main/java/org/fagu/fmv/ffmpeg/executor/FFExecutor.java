package org.fagu.fmv.ffmpeg.executor;

/*
 * #%L
 * fmv-ffmpeg
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.ffmpeg.exception.ExecuteIOException;
import org.fagu.fmv.ffmpeg.operation.FFMPEGProgressReadLine;
import org.fagu.fmv.ffmpeg.operation.LibLogReadLine;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.operation.OperationListener;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.exec.BufferedReadLine;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.FMVCommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.exec.MultiReadLine;
import org.fagu.fmv.soft.exec.ReadLine;
import org.fagu.fmv.soft.ffmpeg.FFMpegSoftProvider;
import org.fagu.fmv.utils.Proxifier;


/**
 * @author f.agu
 */
public class FFExecutor<R> {

	private final Operation<R, ?> operation;

	private FFMPEGProgressReadLine ffmpegProgressReadLine;

	private final List<String> outputs;

	private Prepare prepare;

	private boolean debug;

	private Consumer<String> outDebugConsumer;

	private Consumer<String> errDebugConsumer;

	private final BufferedReadLine outputReadLine;

	private final List<ReadLine> outReadLines, errReadLines, readLines;

	private final List<OperationListener> listeners;

	private final Proxifier<OperationListener> proxifier;

	private final OperationListener proxyOperationListener;

	private final List<FFExecListener> ffExecListeners;

	private final LibLogReadLine libLogReadLine;

	private final List<FFExecFallback> fallbacks;

	private final FFMPEGExecutorBuilder ffmpegExecutorBuilder;

	/**
	 * @param operation
	 */
	public FFExecutor(Operation<R, ?> operation) {
		this(operation, null);
	}

	/**
	 * @param operation
	 * @param ffmpegExecutorBuilder
	 */
	public FFExecutor(Operation<R, ?> operation, FFMPEGExecutorBuilder ffmpegExecutorBuilder) {
		this.operation = Objects.requireNonNull(operation);
		this.ffmpegExecutorBuilder = ffmpegExecutorBuilder;
		if(operation.getFFName().equals(FFMpegSoftProvider.NAME) && ! operation.containsGlobalParameter("nostats")) {
			ffmpegProgressReadLine = new FFMPEGProgressReadLine();
		}
		outputs = new ArrayList<>();
		outputReadLine = new BufferedReadLine(outputs);
		listeners = new ArrayList<>(operation.getListeners());

		outReadLines = new ArrayList<>();
		errReadLines = new ArrayList<>();
		readLines = new ArrayList<>();

		proxifier = new Proxifier<>(OperationListener.class).addAll(listeners);
		proxyOperationListener = proxifier.proxify();

		proxyOperationListener.eventCreate(this);

		libLogReadLine = new LibLogReadLine();
		operation.getLibLogs().stream().forEach(ll -> libLogReadLine.add(ll.getLibLogFilter(), ll));

		fallbacks = new ArrayList<>();
		ffExecListeners = new ArrayList<>();

		ServiceLoader.load(FFExecFallback.class).forEach(fallbacks::add);

		outDebugConsumer = line -> System.out.println("OUT  " + line);
		errDebugConsumer = line -> System.out.println("ERR  " + line);
	}

	/**
	 * @return the operation
	 */
	public Operation<R, ?> getOperation() {
		return operation;
	}

	/**
	 * @return
	 */
	public Progress getProgress() {
		return ffmpegProgressReadLine;
	}

	/**
	 * @return
	 */
	public void debug() {
		debug(true);
	}

	/**
	 * @param debug
	 * @return
	 */
	public void debug(boolean debug) {
		debug(debug, null, null);
	}

	/**
	 * @param debug
	 * @param outDebugConsumer
	 * @param errDebugConsumer
	 * @return
	 */
	public void debug(boolean debug, Consumer<String> outDebugConsumer, Consumer<String> errDebugConsumer) {
		this.debug = debug;
		if(outDebugConsumer != null) {
			this.outDebugConsumer = outDebugConsumer;
		}
		if(errDebugConsumer != null) {
			this.errDebugConsumer = errDebugConsumer;
		}
	}

	/**
	 * @param readLine
	 */
	public void addReadLineOnOut(ReadLine readLine) {
		outReadLines.add(Objects.requireNonNull(readLine));
	}

	/**
	 * @param readLine
	 */
	public void addReadLineOnErr(ReadLine readLine) {
		errReadLines.add(Objects.requireNonNull(readLine));
	}

	/**
	 * @param readLine
	 */
	public void addReadLine(ReadLine readLine) {
		readLines.add(Objects.requireNonNull(readLine));
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public String getCommandLine() throws IOException {
		List<String> arguments = operation.toArguments();

		File fffile = Soft.search(operation.getFFName()).getFile();
		if(fffile == null) {
			throw new IOException("FFName " + operation.getFFName() + " not found or not declared. Use FFLocator.");
		}

		arguments.add(0, fffile.getAbsolutePath());
		return CommandLineUtils.toLine(arguments);
	}

	/**
	 * @return
	 */
	public BufferedReadLine getOutputReadLine() {
		return outputReadLine;
	}

	/**
	 * @param fallback
	 */
	public void addFallback(FFExecFallback fallback) {
		fallbacks.add(Objects.requireNonNull(fallback));
	}

	/**
	 * @param fmvExecListener
	 */
	public void addListener(FFExecListener ffExecListener) {
		ffExecListeners.add(Objects.requireNonNull(ffExecListener));
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Prepare prepare() throws IOException {
		if(prepare != null) {
			throw new RuntimeException("Already prepared");
		}
		return prepare = new Prepare();
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Executed<R> execute() throws IOException {
		if(prepare != null) {
			throw new RuntimeException("Already executed");
		}
		prepare = new Prepare();
		return prepare.execute();
	}

	/**
	 * @param executorService
	 * @return
	 * @throws IOException
	 */
	public FFFuture<Integer> executeAsynchronous(ExecutorService executorService) throws IOException {
		if(prepare != null) {
			throw new RuntimeException("Already executed");
		}
		prepare = new Prepare();
		return prepare.executeAsynchronous(executorService);
	}

	// ************************************************

	/**
	 * @return
	 */
	protected ReadLine getOutReadLine() {
		if(operation.containsGlobalParameter("nostats")) {
			return null;
		}

		List<ReadLine> lines = new ArrayList<>();
		lines.add(outputReadLine);
		ReadLine readLine = operation.getOutReadLine();
		if(readLine != null) {
			lines.add(readLine);
		}
		if(debug) {
			lines.add(outDebugConsumer::accept);
		}
		if( ! outReadLines.isEmpty()) {
			lines.addAll(outReadLines);
		}
		if( ! readLines.isEmpty()) {
			lines.addAll(readLines);
		}
		return MultiReadLine.createWith(lines);
	}

	/**
	 * @return
	 */
	protected ReadLine getErrReadLine() {
		List<ReadLine> lines = new ArrayList<>();
		lines.add(outputReadLine);
		ReadLine readLine = operation.getErrReadLine();
		if(readLine != null) {
			lines.add(readLine);
		}
		if(ffmpegProgressReadLine != null) {
			lines.add(ffmpegProgressReadLine);
		}
		if(debug) {
			lines.add(errDebugConsumer::accept);
		}
		if( ! libLogReadLine.isEmpty()) {
			lines.add(libLogReadLine);
		}
		if( ! errReadLines.isEmpty()) {
			lines.addAll(errReadLines);
		}
		if( ! readLines.isEmpty()) {
			lines.addAll(readLines);
		}
		return MultiReadLine.createWith(lines);
	}

	/**
	 * @param fmvExecutor
	 */
	protected void populateWithListeners(FMVExecutor fmvExecutor) {
		ffExecListeners.forEach(fmvExecutor::addListener);
		listeners.forEach(fmvExecutor::addListener);
	}

	/**
	 * @param exitValue
	 * @param startTime
	 * @param endTime
	 * @param result
	 * @return
	 */
	protected Executed<R> createExecuted(int exitValue, long startTime, long endTime, R result) {
		return new Executed<R>() {

			/**
			 * @see org.fagu.fmv.ffmpeg.executor.Executed#getExitValue()
			 */
			@Override
			public int getExitValue() {
				return exitValue;
			}

			/**
			 * @see org.fagu.fmv.ffmpeg.executor.Executed#getStartTime()
			 */
			@Override
			public long getStartTime() {
				return startTime;
			}

			/**
			 * @see org.fagu.fmv.ffmpeg.executor.Executed#getEndTime()
			 */
			@Override
			public long getEndTime() {
				return endTime;
			}

			/**
			 * @see org.fagu.fmv.ffmpeg.executor.Executed#getDurationInMilliseconds()
			 */
			@Override
			public long getDurationInMilliseconds() {
				return endTime - startTime;
			}

			/**
			 * @see org.fagu.fmv.ffmpeg.executor.Executed#getResult()
			 */
			@Override
			public R getResult() {
				return result;
			}
		};
	}

	// ---------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public class Prepare {

		private List<String> arguments;

		private File ffFile;

		private CommandLine commandLine;

		private FMVExecutor executor;

		/**
		 * @throws IOException
		 */
		private Prepare() throws IOException {
			ffFile = Soft.search(operation.getFFName()).getFile();
			if(ffFile == null) {
				throw new FileNotFoundException("FFName " + operation.getFFName() + " not found or not declared. Use FFLocator.");
			}
		}

		/**
		 * @return
		 */
		public FMVExecutor getExecutor() {
			if(executor == null) {
				executor = FMVExecutor.create(ffFile.getParentFile(), getOutReadLine(), getErrReadLine());
				populateWithListeners(executor);
			}
			return executor;
		}

		/**
		 * @return
		 */
		public CommandLine getCommandLine() {
			if(commandLine == null) {
				commandLine = FMVCommandLine.create(ffFile, getArguments());
			}
			return commandLine;
		}

		/**
		 * @return
		 */
		public List<String> getArguments() {
			if(arguments == null) {
				arguments = Collections.unmodifiableList(operation.toArguments());
			}
			return arguments;
		}

		/**
		 * @return
		 * @throws IOException
		 */
		public Executed<R> execute() throws IOException {
			try {
				return _execute();
			} catch(IOException e) {
				FFExecListener ffExecListener = new Proxifier<>(FFExecListener.class).addAll(ffExecListeners).proxify();
				ffExecListener.eventExecFailed(e, executor, getCommandLine());
				Executed<R> runFallbacks = runFallbacks(e, ffExecListener);
				if(runFallbacks != null) {
					return runFallbacks;
				}
				throw new ExecuteIOException(e, ffFile.getPath() + ' ' + getArguments().toString(), outputs);
			}
		}

		/**
		 * @param executorService
		 * @return
		 * @throws IOException
		 */
		public FFFuture<Integer> executeAsynchronous(ExecutorService executorService) throws IOException {
			return new FFFuture<>(getExecutor().executeAsynchronous(getCommandLine(), executorService));
		}

		// **************************************************

		/**
		 *
		 */
		protected void reset() {
			arguments = null;
			commandLine = null;
			executor = null;
		}

		/**
		 * @param e
		 * @param ffExecListener
		 * @return
		 */
		protected Executed<R> runFallbacks(IOException e, FFExecListener ffExecListener) throws IOException {
			if(fallbacks.isEmpty()) {
				return null;
			}
			FFEnv ffEnv = new FFEnv() {

				@Override
				public FFMPEGExecutorBuilder geExecutorBuilder() {
					return ffmpegExecutorBuilder;
				}

				@Override
				public Operation<?, ?> getOperation() {
					return operation;
				}

				@SuppressWarnings("unchecked")
				@Override
				public FFExecutor<Object> getExecutor() {
					return (FFExecutor<Object>)FFExecutor.this;
				}
			};

			List<FFExecFallback> ffs = new ArrayList<>(fallbacks.size());
			for(FFExecFallback fallback : fallbacks) {
				reset();
				try {
					if(fallback.prepare(ffEnv, e)) {
						ffs.add(fallback);
					}
				} catch(IOException fbe) {
					throw new ExecuteIOException(e, ffFile.getPath() + ' ' + getArguments().toString(), outputs);
				}
			}
			if( ! ffs.isEmpty()) {
				try {
					ffExecListener.eventPreExecFallbacks(executor, getCommandLine(), ffs);
					return _execute();
				} catch(IOException fbe) {
					throw new ExecuteIOException(e, ffFile.getPath() + ' ' + getArguments().toString(), outputs);
				}
			}
			ffExecListener.eventFallbackNotFound(executor, getCommandLine(), Collections.unmodifiableList(outputs));
			return null;
		}

		/**
		 * @return
		 * @throws IOException
		 */
		protected Executed<R> _execute() throws IOException {
			final long startTime = System.currentTimeMillis();
			int exitValue = getExecutor().execute(getCommandLine());
			final long endTime = System.currentTimeMillis();
			return createExecuted(exitValue, startTime, endTime, operation.getResult());
		}

	}

	// ---------------------------------------------------------

}
