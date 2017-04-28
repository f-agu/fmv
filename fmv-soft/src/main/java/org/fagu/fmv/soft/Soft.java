package org.fagu.fmv.soft;

/*
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2016 fagu
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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.fagu.fmv.soft.exec.BufferedReadLine;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.ExecHelper;
import org.fagu.fmv.soft.exec.FMVCommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.exec.WrapFuture;
import org.fagu.fmv.soft.exec.exception.ExceptionConsumer;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzers;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownConsumer;
import org.fagu.fmv.soft.find.Founds;
import org.fagu.fmv.soft.find.Locator;
import org.fagu.fmv.soft.find.Locators;
import org.fagu.fmv.soft.find.PlateformFileFilter;
import org.fagu.fmv.soft.find.SoftFindListener;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftInfo;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.SoftTester;
import org.fagu.fmv.soft.utils.Proxifier;
import org.fagu.fmv.utils.order.OrderComparator;


/**
 * @author f.agu
 */
public class Soft {

	private static final Map<String, Soft> SOFT_NAME_CACHE = new HashMap<>();

	// -----------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class SoftSearch {

		private final SoftName softName;

		private SoftLocator softLocator;

		private FileFilter fileFilter;

		private List<SoftFindListener> softFindListeners;

		/**
		 * @param softProvider
		 */
		private SoftSearch(SoftProvider softProvider) {
			softName = softProvider.getSoftName();
			fileFilter = softProvider.getFileFilter();
			softLocator = softProvider.getSoftLocator();
			softFindListeners = new ArrayList<>();
		}

		/**
		 * @param softLocator
		 * @return
		 */
		public SoftSearch withLocator(SoftLocator softLocator) {
			this.softLocator = Objects.requireNonNull(softLocator);
			return this;
		}

		/**
		 * @param softPolicy
		 * @return
		 */
		public SoftSearch withPolicy(SoftPolicy<?, ?, ?> softPolicy) {
			softLocator.setSoftPolicy(softPolicy);
			return this;
		}

		/**
		 * @param fileFilter
		 * @return
		 */
		public SoftSearch withFileFilter(FileFilter fileFilter) {
			this.fileFilter = fileFilter;
			return this;
		}

		/**
		 * @param softFindListener
		 * @return
		 */
		public SoftSearch withListener(SoftFindListener softFindListener) {
			if(softFindListener != null) {
				softFindListeners.add(softFindListener);
			}
			return this;
		}

		/**
		 * @param softFindListeners
		 * @return
		 */
		public SoftSearch withListeners(Collection<SoftFindListener> softFindListeners) {
			if(softFindListeners != null) {
				softFindListeners.stream().filter(Objects::nonNull).forEach(this.softFindListeners::add);
			}
			return this;
		}

		/**
		 * @return
		 */
		public Soft search() {
			Founds founds = softLocator.find(softName, fileFilter);
			return createAndfireEventFound(founds, softLocator);
		}

		/**
		 * @param softTester
		 * @return
		 */
		public Soft search(SoftTester softTester) {
			Founds founds = softLocator.find(softName, softTester, fileFilter);
			return createAndfireEventFound(founds, softLocator);
		}

		/**
		 * @param softFoundFactory
		 * @return
		 */
		public Soft search(SoftFoundFactory softFoundFactory) {
			Founds founds = softLocator.find(softName, (file, locator, softPolicy) -> {
				try {
					SoftFound softFound = softFoundFactory.create(file, locator, softPolicy);
					if(softFound == null) {
						return SoftFound.foundBadSoft(file);
					}
					return softFound;
				} catch(ExecutionException e) {
					return SoftFound.foundError(file, e.getMessage()).setLocalizedBy(locator.toString());
				} catch(IOException e) {
					return SoftFound.foundError(file, e.getMessage()).setLocalizedBy(locator.toString());
				}
			}, fileFilter);
			return createAndfireEventFound(founds, softLocator);
		}

		/**
		 * @param founds
		 * @param softLocator
		 * @return
		 */
		private Soft createAndfireEventFound(Founds founds, SoftLocator softLocator) {
			Soft soft = softName.create(founds);

			Proxifier<SoftFindListener> proxifier = new Proxifier<>(SoftFindListener.class);
			proxifier.addAll(softFindListeners);
			ServiceLoader.load(SoftFindListener.class).forEach(proxifier::add);
			SoftFindListener softFindListener = proxifier.proxify();

			softFindListener.eventFound(softLocator, soft);
			return soft;
		}

	}

	// -----------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class SoftExecutor extends ExecHelper<SoftExecutor> {

		private final SoftProvider softProvider;

		private final File execFile;

		private final List<String> parameters;

		private final List<ExecListener> execListeners;

		private ExceptionKnownConsumer exceptionKnowConsumer;

		private ExceptionConsumer exceptionConsumer;

		/**
		 * @param softProvider
		 * @param execFile
		 * @param parameters
		 */
		public SoftExecutor(SoftProvider softProvider, File execFile, List<String> parameters) {
			this.softProvider = Objects.requireNonNull(softProvider);
			this.execFile = Objects.requireNonNull(execFile);
			this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters)); // defensive copy
			this.execListeners = new ArrayList<>();
		}

		/**
		 * @param execListener
		 * @return
		 */
		public SoftExecutor addListener(ExecListener execListener) {
			execListeners.add(Objects.requireNonNull(execListener));
			return this;
		}

		/**
		 * @param commandLineConsumer
		 * @return
		 */
		public SoftExecutor logCommandLine(Consumer<String> commandLineConsumer) {
			return addListener(new ExecListener() {

				@Override
				public void eventPrepare(String cmdLineStr) {
					if(commandLineConsumer != null) {
						commandLineConsumer.accept(cmdLineStr);
					}
				}
			});
		}

		/**
		 * @param exceptionKnowConsumer
		 * @return
		 */
		public SoftExecutor ifExceptionIsKnownDo(ExceptionKnownConsumer exceptionKnowConsumer) {
			if(softProvider.getExceptionKnownAnalyzerClass() != null) {
				this.exceptionKnowConsumer = exceptionKnowConsumer;
			}
			return this;
		}

		/**
		 * @param exceptionConsumer
		 * @return
		 */
		public SoftExecutor ifExceptionDo(ExceptionConsumer exceptionConsumer) {
			this.exceptionConsumer = exceptionConsumer;
			return this;
		}

		/**
		 * @return
		 */
		public CommandLine getCommandLine() {
			return FMVCommandLine.create(execFile, parameters);
		}

		/**
		 * @return Executed time in milliseconds
		 * @throws IOException
		 */
		public long execute() throws IOException {
			return execute((fmvExecutor, commandLine, execListener, readLineList) -> {
				final String cmdLineStr = CommandLineUtils.toLine(commandLine);
				long startTime = System.currentTimeMillis();
				long time = 0;
				try {
					execListener.eventExecuting(cmdLineStr);
					int exitValue = fmvExecutor.execute(commandLine);
					time = System.currentTimeMillis() - startTime;
					execListener.eventExecuted(cmdLineStr, exitValue, time);
				} catch(ExecuteException e) {
					FMVExecuteException fmvExecuteException = new FMVExecuteException(softProvider.getExceptionKnownAnalyzerClass(), e, cmdLineStr,
							readLineList);
					execListener.eventException(fmvExecuteException);
					ExceptionKnownAnalyzers.doOrThrows(softProvider
							.getExceptionKnownAnalyzerClass(), fmvExecuteException, exceptionKnowConsumer, exceptionConsumer);
				}
				return time;
			});
		}

		/**
		 * @param executorService
		 * @return
		 * @throws IOException
		 */
		public Future<BackgroundExecuted> executeInBackground(ExecutorService executorService) throws IOException {
			return execute((fmvExecutor, commandLine, execListener, readLineList) -> {
				final String cmdLineStr = CommandLineUtils.toLine(commandLine);
				final AtomicLong startTime = new AtomicLong();
				final AtomicLong time = new AtomicLong();
				return new WrapFuture<>(fmvExecutor.executeAsynchronous(commandLine, executorService,
						// before
						() -> {
							startTime.set(System.currentTimeMillis());
							execListener.eventExecuting(cmdLineStr);
						},
						// after
						exitValue -> {
							time.set(System.currentTimeMillis() - startTime.get());
							execListener.eventExecuted(cmdLineStr, exitValue, time.get());
						},
						// exception
						exception -> {
							if(exception instanceof ExecuteException) {
								ExecuteException e = (ExecuteException)exception;
								FMVExecuteException fmvExecuteException = new FMVExecuteException(softProvider.getExceptionKnownAnalyzerClass(), e,
										cmdLineStr, readLineList);
								execListener.eventException(fmvExecuteException);
								ExceptionKnownAnalyzers.doOrThrows(softProvider
										.getExceptionKnownAnalyzerClass(), fmvExecuteException, exceptionKnowConsumer, exceptionConsumer);
							}
						}),
						exitValue -> new BackgroundExecuted(exitValue, time.get()));
			});
		}

		// -------------------------------------------------------------

		/**
		 * @author fagu
		 */
		public static class BackgroundExecuted {

			private final int exitValue;

			private final long executeTime;

			private BackgroundExecuted(int exitValue, long executeTime) {
				this.exitValue = exitValue;
				this.executeTime = executeTime;
			}

			public int getExitValue() {
				return exitValue;
			}

			public long getExecuteTime() {
				return executeTime;
			}

			@Override
			public String toString() {
				return "BackgroundExecuted[exit: " + exitValue + " ; time: " + executeTime + "ms]";
			}

		}

		// -------------------------------------------------------------

		/**
		 * @param executorService
		 * @return
		 * @throws IOException
		 */
		private <R> R execute(ForExec<R> forExec) throws IOException {
			ExecListener execListener = new Proxifier<>(ExecListener.class).addAll(execListeners).proxify();

			CommandLine commandLine = getCommandLine();
			String cmdLineStr = CommandLineUtils.toLine(commandLine);
			execListener.eventPrepare(cmdLineStr);

			List<String> readLineList = new ArrayList<>();
			BufferedReadLine bufferedReadLine = new BufferedReadLine(readLineList);
			FMVExecutor fmvExecutor = createFMVExecutor(execFile.getParentFile(), bufferedReadLine);
			applyCustomizeExecutor(fmvExecutor);

			return forExec.exec(fmvExecutor, commandLine, execListener, readLineList);
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

	// -----------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static interface ExecListener {

		/**
		 * @param cmdLineStr
		 */
		default void eventPrepare(String cmdLineStr) {}

		/**
		 * @param cmdLineStr
		 */
		default void eventExecuting(String cmdLineStr) {}

		/**
		 * @param cmdLineStr
		 * @param exitValue
		 * @param timeMilleseconds
		 */
		default void eventExecuted(String cmdLineStr, int exitValue, long timeMilleseconds) {}

		/**
		 * @param fmvExecuteException
		 */
		default void eventException(FMVExecuteException fmvExecuteException) {}
	}

	// -----------------------------------------------------------

	private final Founds founds;

	private final SoftProvider softProvider;

	public Soft(Founds founds, SoftProvider softProvider) {
		this.founds = Objects.requireNonNull(founds);
		this.softProvider = Objects.requireNonNull(softProvider);
	}

	// =============

	/**
	 * @param execFile
	 * @return
	 * @throws IOException
	 */
	public static Soft withExecFile(String execFile) throws IOException {
		File file = new File(execFile);
		if( ! execFile.contains("/") && ! execFile.contains("\\") && ! file.exists()) {
			// search in ENV PATH
			Locators locators = new Locators(PlateformFileFilter.getFileFilter(execFile));
			Locator locator = locators.byEnvPath();
			List<File> locatedFiles = locator.locate(null);
			if(locatedFiles.isEmpty()) {
				throw new FileNotFoundException(execFile);
			}
			Collections.sort(locatedFiles);
			file = locatedFiles.get(0);
		}
		return withExecFile(file);
	}

	/**
	 * @param softName
	 * @return
	 */
	public static Soft withExecFile(File file) throws IOException {
		if( ! file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		if( ! file.isFile()) {
			throw new IOException("It's not a file: " + file.getAbsolutePath());
		}
		Path path = file.toPath();
		if( ! Files.isExecutable(path)) {
			throw new IOException("Cannot execute: " + file.getAbsolutePath());
		}
		SoftProvider softProvider = new SoftProvider(file.getName()) {

			@Override
			public SoftPolicy<?, ?, ?> getSoftPolicy() {
				return null;
			}

			@Override
			public SoftFoundFactory createSoftFoundFactory() {
				throw new RuntimeException("Not available !");
			}
		};
		TreeSet<SoftFound> founds = new TreeSet<>(Collections.singleton(SoftFound.found(file)));
		return new Soft(new Founds(softProvider.getSoftName(), founds, null), softProvider);
	}

	/**
	 * @param name
	 * @return
	 */
	public static Soft search(String name) {
		Soft soft = SOFT_NAME_CACHE.get(name);
		if(soft != null) {
			return soft;
		}
		soft = SoftProvider.getSoftProviders()
				.filter(sp -> sp.getName().equalsIgnoreCase(name))
				.sorted(Collections.reverseOrder(OrderComparator.INSTANCE))
				.map(SoftProvider::search)
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Soft \"" + name + "\" undefined"));
		SOFT_NAME_CACHE.put(name, soft);
		return soft;
	}

	/**
	 * @return
	 */
	public static Stream<Soft> searchAll() {
		return SoftProvider.getSoftProviders().map(SoftProvider::search);
	}

	/**
	 * @param softSearchConsumer
	 * @return
	 */
	public static Stream<Soft> searchAll(Consumer<SoftSearch> softSearchConsumer) {
		return SoftProvider.getSoftProviders().map(sp -> sp.search(softSearchConsumer));
	}

	/**
	 * @param softName
	 * @return
	 */
	public static SoftSearch with(SoftProvider softProvider) {
		return new SoftSearch(softProvider);
	}

	// =============

	/**
	 * @return
	 */
	public SoftName getSoftName() {
		return founds.getSoftName();
	}

	/**
	 * @return
	 */
	public String getName() {
		return getSoftName().getName();
	}

	/**
	 * @return
	 */
	public boolean isFound() {
		return founds.isFound();
	}

	/**
	 * @return
	 */
	public Founds getFounds() {
		return founds;
	}

	/**
	 * @return
	 */
	public SoftFound getFirstFound() {
		if( ! founds.isFound()) {
			throw new IllegalStateException("Soft " + getName() + " not found");
		}
		return founds.getFirstFound();
	}

	/**
	 * @return
	 */
	public SoftInfo getFirstInfo() {
		return getFirstFound().getSoftInfo();
	}

	/**
	 * @return
	 */
	public File getFile() {
		return getFirstFound().getFile();
	}

	/**
	 * @return
	 */
	public SoftExecutor withoutParameter() {
		return withParameters(Collections.emptyList());
	}

	/**
	 * @param param1
	 * @param otherPparameters
	 * @return
	 */
	public SoftExecutor withParameters(String param1, String... otherPparameters) {
		List<String> params = new ArrayList<>(1 + otherPparameters.length);
		params.add(param1);
		params.addAll(Arrays.asList(otherPparameters));
		return withParameters(params);
	}

	/**
	 * @param parameters
	 * @return
	 */
	public SoftExecutor withParameters(List<String> parameters) {
		return softProvider.createSoftExecutor(this, getFile(), parameters);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public long execute() throws IOException {
		return withoutParameter().execute();
	}

	/**
	 * @return
	 */
	public SoftProvider getSoftProvider() {
		return softProvider;
	}

	/**
	 * @return
	 */
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		SoftPolicy<?, ?, ?> softPolicy = founds.getSoftPolicy();
		return softPolicy != null ? softPolicy : softProvider.getSoftPolicy();
	}

	/**
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(getName());
		if(getFounds().isFound()) {
			buf.append(" (").append(getFile().getAbsolutePath()).append(')');
		} else {
			buf.append(" <not found>");
		}
		return buf.toString();
	}

}
