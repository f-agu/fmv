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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.ffmpeg.exception.FFExceptionKnownAnalyzer;
import org.fagu.fmv.ffmpeg.exception.FFSimpleExceptionKnownAnalyzer;
import org.fagu.fmv.ffmpeg.operation.FFMPEGProgressReadLine;
import org.fagu.fmv.ffmpeg.operation.LibLogReadLine;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.operation.OperationListener;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.ffmpeg.operation.ProgressReadLine;
import org.fagu.fmv.ffmpeg.soft.FFMpegSoftProvider;
import org.fagu.fmv.ffmpeg.soft.FFSoft;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.soft.exec.BufferedReadLine;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.exec.MultiReadLine;
import org.fagu.fmv.soft.exec.ReadLine;
import org.fagu.fmv.soft.exec.UnmodifiableCommandLine;
import org.fagu.fmv.soft.exec.exception.ExceptionConsumer;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzers;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownConsumer;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;
import org.fagu.fmv.utils.Proxifier;
import org.fagu.fmv.utils.collection.LimitedLastQueue;
import org.fagu.fmv.utils.io.InputStreamSupplier;
import org.fagu.fmv.utils.io.OutputStreamSupplier;


/**
 * @author f.agu
 */
public class FFExecutor<R> {

	private final Operation<R, ?> operation;

	private ProgressReadLine progressReadLine;

	private final List<String> outputs;

	private Prepare prepare;

	private boolean debug;

	private Consumer<String> outDebugConsumer;

	private Consumer<String> errDebugConsumer;

	private Soft customSoft;

	private final BufferedReadLine outputReadLine;

	private final List<ReadLine> outReadLines;

	private final List<ReadLine> errReadLines;

	private final List<ReadLine> readLines;

	private final List<OperationListener> listeners;

	private final Proxifier<OperationListener> proxifier;

	private final OperationListener proxyOperationListener;

	private final List<FFExecListener> ffExecListeners;

	private final LibLogReadLine libLogReadLine;

	private final List<FFExecFallback> fallbacks;

	private final FFMPEGExecutorBuilder ffmpegExecutorBuilder;

	private ExceptionKnownConsumer exceptionKnownConsumer;

	private ExceptionConsumer exceptionConsumer;

	private final List<Consumer<SoftExecutor>> customizeSoftExecutors;

	private InputStreamSupplier inputStreamSupplier;

	private OutputStreamSupplier outputStreamSupplier;

	private Function<CommandLine, String> toStringCommandLine = CommandLineUtils::toLine;

	public FFExecutor(Operation<R, ?> operation) {
		this(operation, null);
	}

	public FFExecutor(Operation<R, ?> operation, FFMPEGExecutorBuilder ffmpegExecutorBuilder) {
		this.operation = Objects.requireNonNull(operation);
		this.ffmpegExecutorBuilder = ffmpegExecutorBuilder;
		if(operation.getFFName().equals(FFMpegSoftProvider.NAME) && ! operation.containsGlobalParameter("nostats")) {
			if(ffmpegExecutorBuilder != null) {
				progressReadLine = ffmpegExecutorBuilder.getFFMPEGOperation().getProgressReadLine();
			}
			if(progressReadLine == null) {
				progressReadLine = new FFMPEGProgressReadLine();
			}
		}
		outputs = new LimitedLastQueue<>(500);
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

		customizeSoftExecutors = new LinkedList<>();
	}

	public Operation<R, ?> getOperation() {
		return operation;
	}

	public Progress getProgress() {
		return progressReadLine;
	}

	public void debug() {
		debug(true);
	}

	public void debug(boolean debug) {
		debug(debug, null, null);
	}

	public void debug(boolean debug, Consumer<String> outDebugConsumer, Consumer<String> errDebugConsumer) {
		this.debug = debug;
		if(outDebugConsumer != null) {
			this.outDebugConsumer = outDebugConsumer;
		}
		if(errDebugConsumer != null) {
			this.errDebugConsumer = errDebugConsumer;
		}
	}

	public void addReadLineOnOut(ReadLine readLine) {
		outReadLines.add(Objects.requireNonNull(readLine));
	}

	public void addReadLineOnErr(ReadLine readLine) {
		errReadLines.add(Objects.requireNonNull(readLine));
	}

	public void addReadLine(ReadLine readLine) {
		readLines.add(Objects.requireNonNull(readLine));
	}

	public String getCommandLineString() throws IOException {
		List<String> arguments = operation.toArguments();
		File fffile = findSoft().getFile();
		if(fffile == null) {
			throw new IOException("FFName " + operation.getFFName() + " not found or not declared. Use FFLocator.");
		}
		CommandLine commandLine = CommandLine.parse("\"" + fffile.getAbsolutePath() + "\"");
		arguments.forEach(arg -> commandLine.addArgument(arg, false));
		return toStringCommandLine.apply(commandLine);
	}

	public BufferedReadLine getOutputReadLine() {
		return outputReadLine;
	}

	public void input(InputStreamSupplier inputStreamSupplier) {
		this.inputStreamSupplier = inputStreamSupplier;
	}

	public void output(OutputStreamSupplier outputStreamSupplier) {
		this.outputStreamSupplier = outputStreamSupplier;
	}

	public void addFallback(FFExecFallback fallback) {
		fallbacks.add(Objects.requireNonNull(fallback));
	}

	public void addListener(FFExecListener ffExecListener) {
		ffExecListeners.add(Objects.requireNonNull(ffExecListener));
	}

	public void ifExceptionIsKnownDo(ExceptionKnownConsumer exceptionKnowConsumer) {
		this.exceptionKnownConsumer = exceptionKnowConsumer;
	}

	public void ifExceptionDo(ExceptionConsumer exceptionConsumer) {
		this.exceptionConsumer = exceptionConsumer;
	}

	public void customizeSoftExecutor(Consumer<SoftExecutor> customizeSoftExecutor) {
		if(customizeSoftExecutor != null) {
			customizeSoftExecutors.add(customizeSoftExecutor);
		}
	}

	public void setToStringCommandLine(Function<CommandLine, String> toStringCommandLine) {
		this.toStringCommandLine = Objects.requireNonNull(toStringCommandLine);
	}

	public void setSoft(Soft soft) {
		this.customSoft = soft;
	}

	public Prepare prepare() throws IOException {
		if(prepare != null) {
			throw new RuntimeException("Already prepared");
		}
		prepare = new Prepare();
		return prepare;
	}

	public FFExecuted<R> execute() throws IOException {
		if(prepare != null) {
			throw new RuntimeException("Already executed");
		}
		prepare = new Prepare();
		return prepare.execute();
	}

	// ************************************************

	protected ReadLine getOutReadLine() {
		if(operation.containsGlobalParameter("nostats")) {
			return null;
		}

		List<ReadLine> lines = new ArrayList<>();
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

	protected ReadLine getErrReadLine() {
		List<ReadLine> lines = new ArrayList<>();
		if(progressReadLine != null) {
			lines.add(progressReadLine);
		}
		ReadLine readLine = operation.getErrReadLine();
		if(readLine != null) {
			lines.add(readLine);
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

	protected void populateWithListeners(FMVExecutor fmvExecutor) {
		ffExecListeners.forEach(fmvExecutor::addListener);
		listeners.forEach(fmvExecutor::addListener);
	}

	// *****************************************************

	private Soft findSoft() {
		return customSoft != null ? customSoft : FFSoft.search(operation.getFFName());
	}

	// ---------------------------------------------------------

	public class Prepare {

		private final Soft soft;

		private Prepare() {
			soft = findSoft();
		}

		public SoftExecutor getSoftExecutor() {
			NoOverwriteDeblock noOverwriteDeblock = new NoOverwriteDeblock();
			SoftExecutor softExecutor = soft.withParameters(operation.toArguments())
					.addOutReadLine(getOutReadLine())
					.addErrReadLine(getErrReadLine())
					.customizeExecutor(FFExecutor.this::populateWithListeners)
					.customizeExecutor(e -> e.addProcessOperator(noOverwriteDeblock))
					.lookReader(noOverwriteDeblock)
					.ifExceptionIsKnownDo(noOverwriteDeblock);

			if(inputStreamSupplier != null) {
				try {
					InputStream inputStream = inputStreamSupplier.getInputStream();
					softExecutor.input(inputStream);
					softExecutor.addCloseable(inputStream);
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
			}
			if(outputStreamSupplier != null) {
				try {
					OutputStream outputStream = outputStreamSupplier.getOutputStream();
					softExecutor.output(outputStream);
					softExecutor.addCloseable(outputStream);
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
			}

			// don't add exceptionKnowConsumer here
			customizeSoftExecutors.forEach(cse -> cse.accept(softExecutor));
			return softExecutor;
		}

		public CommandLine getCommandLine() {
			return new UnmodifiableCommandLine(soft.withParameters(operation.toArguments()).getCommandLine());
		}

		public FFExecuted<R> execute() throws IOException {
			try {
				return _execute();
			} catch(IOException e) {
				FFExecListener ffExecListener = new Proxifier<>(FFExecListener.class).addAll(ffExecListeners).proxify();
				ffExecListener.eventExecFailed(e, new UnmodifiableCommandLine(getCommandLine()));
				FFExecuted<R> runFallbacks = runFallbacks(e, ffExecListener);
				if(runFallbacks != null) {
					return runFallbacks;
				}
				ExceptionKnownAnalyzers.doOrThrows(FFExceptionKnownAnalyzer.class, e, exceptionKnownConsumer, exceptionConsumer);
				return null;
			}
		}

		// **************************************************

		protected FFExecuted<R> runFallbacks(IOException originalException, FFExecListener ffExecListener) throws IOException {
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
				try {
					if(fallback.prepare(ffEnv, originalException)) {
						ffs.add(fallback);
					}
				} catch(IOException fbe) {
					throw new FMVExecuteException(FFExceptionKnownAnalyzer.class, 0, originalException,
							toStringCommandLine.apply(getCommandLine()), outputs);
				}
			}
			if( ! ffs.isEmpty()) {
				try {
					ffExecListener.eventPreExecFallbacks(getCommandLine(), ffs);
					return _execute();
				} catch(IOException fbe) {
					throw new FMVExecuteException(FFSimpleExceptionKnownAnalyzer.class, 0, originalException, CommandLineUtils
							.toLine(getCommandLine()),
							outputs);
				}
			}
			ffExecListener.eventFallbackNotFound(getCommandLine(), Collections.unmodifiableList(outputs));
			return null;
		}

		protected FFExecuted<R> _execute() throws IOException {
			org.fagu.fmv.soft.SoftExecutor.Executed executed = getSoftExecutor().execute();
			return FFExecuted.create(executed, operation.getResult());
		}

	}

	// ---------------------------------------------------------

}
