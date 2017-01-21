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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.fagu.fmv.soft.exec.BufferedReadLine;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.ExecHelper;
import org.fagu.fmv.soft.exec.FMVCommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.exec.exception.ExceptionKnowConsumer;
import org.fagu.fmv.soft.exec.exception.ExceptionKnown;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzers;
import org.fagu.fmv.soft.find.FoundStrategy;
import org.fagu.fmv.soft.find.Founds;
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

	// -----------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class SoftSearch {

		private final SoftName softName;

		private SoftLocator softLocator;

		private List<SoftFindListener> softFindListeners;

		private FoundStrategy foundStrategy;

		/**
		 * @param softName
		 */
		private SoftSearch(SoftName softName) {
			this.softName = Objects.requireNonNull(softName);
			softFindListeners = new ArrayList<>();
		}

		/**
		 * @param softLocator
		 * @return
		 */
		public SoftSearch with(SoftLocator softLocator) {
			this.softLocator = Objects.requireNonNull(softLocator);
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
		 * @param foundStrategy
		 * @return
		 */
		public SoftSearch withFoundStrategy(FoundStrategy foundStrategy) {
			this.foundStrategy = foundStrategy;
			return this;
		}

		/**
		 * @return
		 */
		public Soft search() {
			SoftLocator softLoc = getSoftLocator();
			Founds founds = softLoc.find(softName);
			return createAndfireEventFound(founds, softLoc);
		}

		/**
		 * @param softTester
		 * @return
		 */
		public Soft search(SoftTester softTester) {
			SoftLocator softLoc = getSoftLocator();
			Founds founds = softLoc.find(softName, softTester);
			return createAndfireEventFound(founds, softLoc);
		}

		/**
		 * @param softFoundFactory
		 * @return
		 */
		public Soft search(SoftFoundFactory softFoundFactory) {
			SoftLocator softLoc = getSoftLocator();
			Founds founds = softLoc.find(softName, file -> {
				try {
					SoftFound softFound = softFoundFactory.create(file);
					if(softFound == null) {
						return SoftFound.foundBadSoft(file);
					}
					return softFound;
				} catch(ExecutionException e) {
					return SoftFound.foundError(file, e.getMessage());
				} catch(IOException e) {
					return SoftFound.foundError(file, e.getMessage());
				}
			});
			return createAndfireEventFound(founds, softLoc);
		}

		// ****************************************************

		/**
		 * @return
		 */
		private SoftLocator getSoftLocator() {
			SoftLocator softLoc = softLocator != null ? softLocator : new SoftLocator();
			if(foundStrategy != null) {
				softLoc.setFoundStrategy(foundStrategy);
			}
			return softLoc;
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

		private ExceptionKnowConsumer exceptionKnowConsumer;

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
		public SoftExecutor ifExceptionIsKnownDo(ExceptionKnowConsumer exceptionKnowConsumer) {
			if(softProvider.getExceptionKnownAnalyzerClass() != null) {
				this.exceptionKnowConsumer = exceptionKnowConsumer;
			}
			return this;
		}

		/**
		 * @return Executed time in milliseconds
		 * @throws IOException
		 */
		public long execute() throws IOException {
			ExecListener execListener = new Proxifier<>(ExecListener.class).addAll(execListeners).proxify();

			CommandLine commandLine = FMVCommandLine.create(execFile, parameters);
			String cmdLineStr = CommandLineUtils.toLine(commandLine);
			execListener.eventPrepare(cmdLineStr);

			List<String> readLineList = new ArrayList<>();
			BufferedReadLine bufferedReadLine = new BufferedReadLine(readLineList);
			FMVExecutor fmvExecutor = createFMVExecutor(execFile.getParentFile(), bufferedReadLine);
			applyCustomizeExecutor(fmvExecutor);

			long startTime = System.currentTimeMillis();
			long time = 0;
			try {
				int exitValue = fmvExecutor.execute(commandLine);
				time = System.currentTimeMillis() - startTime;
				execListener.eventExecuted(cmdLineStr, exitValue, time);
			} catch(ExecuteException e) {
				FMVExecuteException fmvExecuteException = new FMVExecuteException(e, cmdLineStr, readLineList);
				execListener.eventException(fmvExecuteException);
				boolean isKnown = false;
				if(exceptionKnowConsumer != null) {
					Optional<ExceptionKnown> known = ExceptionKnownAnalyzers.getKnown(softProvider.getExceptionKnownAnalyzerClass(), e);
					if(known.isPresent()) {
						isKnown = true;
						exceptionKnowConsumer.accept(known.get());
					}
				}
				if( ! isKnown) {
					throw fmvExecuteException;
				}
			}
			return time;
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

	/**
	 * @param founds
	 * @param softProvider
	 */
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
		return withExecFile(new File(execFile));
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
		return new Soft(new Founds(softProvider.getSoftName(), founds), softProvider);
	}

	/**
	 * @param name
	 * @return
	 */
	public static Soft search(String name) {
		return SoftProvider.getSoftProviders() //
				.filter(sp -> sp.getName().equalsIgnoreCase(name)) //
				.sorted(Collections.reverseOrder(OrderComparator.INSTANCE)) //
				.map(SoftProvider::search) //
				.findFirst() //
				.orElseThrow(() -> new RuntimeException("Soft \"" + name + "\" undefined"));
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
	public static SoftSearch with(SoftName softName) {
		return new SoftSearch(softName);
	}

	// =============

	/**
	 * @return the softTool
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
