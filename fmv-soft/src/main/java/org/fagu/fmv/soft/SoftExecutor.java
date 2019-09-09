package org.fagu.fmv.soft;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.fagu.fmv.soft.exec.BufferedReadLine;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.ExecHelper;
import org.fagu.fmv.soft.exec.FMVCommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.exec.LookReader;
import org.fagu.fmv.soft.exec.PIDProcessOperator;
import org.fagu.fmv.soft.exec.ReadLine;
import org.fagu.fmv.soft.exec.UnmodifiableCommandLine;
import org.fagu.fmv.soft.exec.WrapFuture;
import org.fagu.fmv.soft.exec.exception.ExceptionConsumer;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzers;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownConsumer;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.utils.Proxifier;
import org.fagu.fmv.utils.collection.LimitedLastQueue;


/**
 * @author f.agu
 */
public class SoftExecutor extends ExecHelper<SoftExecutor> {

	private final SoftProvider softProvider;

	private final File execFile;

	private final List<String> parameters;

	private final List<ExecListener> execListeners;

	private final Map<String, String> environmentMap;

	private ExceptionKnownConsumer exceptionKnowConsumer;

	private ExceptionConsumer exceptionConsumer;

	private ExecuteDelegate executeDelegate;

	private Supplier<List<String>> bufferedReadLineSupplier = () -> new LimitedLastQueue<>(500);

	private Function<CommandLine, String> toStringCommandLine = CommandLineUtils::toLine;

	public SoftExecutor(SoftProvider softProvider, File execFile, List<String> parameters) {
		this.softProvider = Objects.requireNonNull(softProvider);
		this.execFile = Objects.requireNonNull(execFile);
		this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters)); // defensive copy
		execListeners = new ArrayList<>();
		environmentMap = new HashMap<>();
	}

	public SoftExecutor addListener(ExecListener execListener) {
		execListeners.add(Objects.requireNonNull(execListener));
		return this;
	}

	public SoftExecutor logCommandLine(Consumer<CommandLine> commandLineConsumer) {
		if(commandLineConsumer == null) {
			return this;
		}
		return addListener(new ExecListener() {

			@Override
			public void eventPrepare(CommandLine commandLine) {
				commandLineConsumer.accept(commandLine);
			}
		});
	}

	public SoftExecutor ifExceptionIsKnownDo(ExceptionKnownConsumer exceptionKnowConsumer) {
		if(softProvider.getExceptionKnownAnalyzerClass() != null) {
			this.exceptionKnowConsumer = exceptionKnowConsumer;
		}
		return this;
	}

	public SoftExecutor ifExceptionDo(ExceptionConsumer exceptionConsumer) {
		this.exceptionConsumer = exceptionConsumer;
		return this;
	}

	public SoftExecutor setBufferedReadLineSupplier(Supplier<List<String>> bufferedReadLineSupplier) {
		this.bufferedReadLineSupplier = Objects.requireNonNull(bufferedReadLineSupplier);
		return this;
	}

	@Override
	public SoftExecutor lookReader(LookReader lookReader) {
		this.lookReader = lookReader;
		return this;
	}

	public SoftExecutor addEnv(String key, String value) {
		environmentMap.put(key, value);
		return this;
	}

	public SoftExecutor addEnvs(Map<String, String> map) {
		environmentMap.putAll(map);
		return this;
	}

	public SoftExecutor setExecuteDelegate(ExecuteDelegate executeDelegate) {
		this.executeDelegate = Objects.requireNonNull(executeDelegate);
		return this;
	}

	public SoftExecutor setToStringCommandLine(Function<CommandLine, String> toStringCommandLine) {
		this.toStringCommandLine = Objects.requireNonNull(toStringCommandLine);
		return this;
	}

	public CommandLine getCommandLine() {
		return FMVCommandLine.create(execFile, parameters);
	}

	public SoftExecutor.Executed execute() throws IOException {
		return execute((fmvExecutor, commandLine, execListener, readLineList) -> {

			long startTime = System.currentTimeMillis();
			long time = 0;
			int exitValue = 0;
			PIDProcessOperator pidProcessOperator = new PIDProcessOperator();
			fmvExecutor.addProcessOperator(pidProcessOperator);
			try {
				execListener.eventExecuting(commandLine);
				exitValue = geExecuteDelegate().execute(fmvExecutor, commandLine);
				time = System.currentTimeMillis() - startTime;
				execListener.eventExecuted(commandLine, exitValue, time);
			} catch(ExecuteException e) {
				final String cmdLineStr = toStringCommandLine.apply(commandLine);
				FMVExecuteException fmvExecuteException = new FMVExecuteException(softProvider.getExceptionKnownAnalyzerClass(), e, cmdLineStr,
						readLineList);
				execListener.eventException(fmvExecuteException);
				ExceptionKnownAnalyzers.doOrThrows(softProvider
						.getExceptionKnownAnalyzerClass(), fmvExecuteException, exceptionKnowConsumer, exceptionConsumer);
			}
			return new Executed(pidProcessOperator.getPID(), exitValue, time);
		});
	}

	public Future<SoftExecutor.Executed> executeInBackground(ExecutorService executorService) throws IOException {
		return execute((fmvExecutor, commandLine, execListener, readLineList) -> {
			final AtomicLong startTime = new AtomicLong();
			final AtomicLong time = new AtomicLong();
			final PIDProcessOperator pidProcessOperator = new PIDProcessOperator();
			fmvExecutor.addProcessOperator(pidProcessOperator);
			return new WrapFuture<>(fmvExecutor.executeAsynchronous(geExecuteDelegate(), commandLine, executorService,
					// before
					() -> {
						startTime.set(System.currentTimeMillis());
						execListener.eventExecuting(commandLine);
					},
					// after
					exitValue -> {
						time.set(System.currentTimeMillis() - startTime.get());
						execListener.eventExecuted(commandLine, exitValue, time.get());
					},
					// exception
					exception -> {
						if(exception instanceof ExecuteException) {
							ExecuteException e = (ExecuteException)exception;
							final String cmdLineStr = toStringCommandLine.apply(commandLine);
							FMVExecuteException fmvExecuteException = new FMVExecuteException(softProvider.getExceptionKnownAnalyzerClass(), e,
									cmdLineStr, readLineList);
							execListener.eventException(fmvExecuteException);
							ExceptionKnownAnalyzers.doOrThrows(softProvider
									.getExceptionKnownAnalyzerClass(), fmvExecuteException, exceptionKnowConsumer, exceptionConsumer);
						}
					}),
					exitValue -> new Executed(pidProcessOperator.getPID(), exitValue, time.get()));
		});
	}

	// -------------------------------------------------------------

	/**
	 * @author fagu
	 */
	public static class Executed {

		private final OptionalLong pid;

		private final int exitValue;

		private final long executeTime;

		private Executed(OptionalLong pid, int exitValue, long executeTime) {
			this.pid = pid;
			this.exitValue = exitValue;
			this.executeTime = executeTime;
		}

		public OptionalLong getPID() {
			return pid;
		}

		public int getExitValue() {
			return exitValue;
		}

		public long getExecuteTime() {
			return executeTime;
		}

		@Override
		public String toString() {
			Duration duration = Duration.ofMillis(executeTime);
			StringBuilder buf = new StringBuilder()
					.append("Executed[");
			pid.ifPresent(id -> buf.append("pid:").append(id).append(" ; "));
			return buf.append("exit: ").append(exitValue).append(" ; time: ").append(duration.toString()).append(']')
					.toString();
		}

	}

	// -------------------------------------------------------------

	private <R> R execute(SoftExecutor.ForExec<R> forExec) throws IOException {
		ExecListener execListener = new Proxifier<>(ExecListener.class).addAll(execListeners).proxify();

		CommandLine commandLine = new UnmodifiableCommandLine(getCommandLine());
		execListener.eventPrepare(commandLine);

		List<String> readLineList = Objects.requireNonNull(bufferedReadLineSupplier.get());
		ReadLine bufferedReadLine = new BufferedReadLine(readLineList);
		FMVExecutor fmvExecutor = createFMVExecutor(execFile.getParentFile(), bufferedReadLine);
		applyCustomizeExecutor(fmvExecutor);

		return forExec.exec(fmvExecutor, commandLine, execListener, readLineList);
	}

	private ExecuteDelegate geExecuteDelegate() {
		if(executeDelegate != null) {
			return executeDelegate;
		}
		if(environmentMap.isEmpty()) {
			return BasicExecuteDelegate.INSTANCE;
		}
		return new EnvironmentExecuteDelegate(environmentMap);
	}

	// --------------------------------------------------

	/**
	 * @param <R>
	 * @author Oodrive
	 * @author f.agu
	 * @created 21 avr. 2017 11:31:39
	 */
	private static interface ForExec<R> {

		R exec(FMVExecutor fmvExecutor, CommandLine commandLine, ExecListener execListener, List<String> readLineList) throws IOException;
	}

}
