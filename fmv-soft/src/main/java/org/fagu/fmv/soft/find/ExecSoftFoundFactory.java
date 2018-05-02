package org.fagu.fmv.soft.find;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.exec.BufferedReadLine;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.ExecHelper;
import org.fagu.fmv.soft.exec.FMVCommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.exec.ReadLine;
import org.fagu.fmv.soft.find.info.VersionDateSoftInfo;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.utils.collection.LimitedLastQueue;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class ExecSoftFoundFactory implements SoftFoundFactory {

	// ------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class ExecSoftFoundFactoryPrepare {

		private final SoftProvider softProvider;

		private ExecSoftFoundFactoryPrepare(SoftProvider softProvider) {
			this.softProvider = Objects.requireNonNull(softProvider);
		}

		public ExecSoftFoundFactoryBuilder withoutParameter() {
			return withParameters();
		}

		public ExecSoftFoundFactoryBuilder withParameters(String... parameters) {
			return new ExecSoftFoundFactoryBuilder(softProvider, Collections.unmodifiableList(new ArrayList<>(Arrays.asList(parameters))));
		}
	}

	// ------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class ExecSoftFoundFactoryBuilder extends ExecHelper<ExecSoftFoundFactoryBuilder> {

		private final SoftProvider softProvider;

		private final List<String> parameters;

		private ParserFactory parserFactory;

		private boolean build;

		private ExecutorFactory executorFactory;

		private Supplier<List<String>> bufferedReadLineSupplier = () -> new LimitedLastQueue<>(500);

		private ExecSoftFoundFactoryBuilder(SoftProvider softProvider, List<String> parameters) {
			this.softProvider = softProvider;
			this.parameters = parameters;

		}

		public ExecSoftFoundFactoryBuilder parseFactory(ParserFactory parserFactory) {
			this.parserFactory = parserFactory;
			return this;
		}

		public ExecSoftFoundFactoryBuilder parseVersion(ParseVersion parseVersion) {
			Objects.requireNonNull(parseVersion);
			return parseFactory((file, softPolicy) -> {
				return new Parser() {

					private Version version;

					@Override
					public void readLine(String line) {
						Version v = parseVersion.readLineAndParse(line);
						if(v != null) {
							version = v;
						}
					}

					@Override
					public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
						return softPolicy.toSoftFound(new VersionSoftInfo(file, softProvider.getName(), version));
					}
				};
			});
		}

		public ExecSoftFoundFactoryBuilder parseVersionDate(ParseVersionDate parseVersiondate) {
			Objects.requireNonNull(parseVersiondate);
			return parseFactory((file, softPolicy) -> {
				return new Parser() {

					private Version version;

					private Date date;

					@Override
					public void readLine(String line) {
						VersionDate versionDate = parseVersiondate.readLineAndParse(line);
						if(versionDate != null) {
							versionDate.getVersion().ifPresent(v -> version = v);
							versionDate.getDate().ifPresent(d -> date = d);
						}
					}

					@Override
					public SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException {
						return softPolicy.toSoftFound(new VersionDateSoftInfo(file, softProvider.getName(), version, date));
					}
				};
			});
		}

		public ExecSoftFoundFactoryBuilder withBufferedReadLineSupplier(Supplier<List<String>> bufferedReadLineSupplier) {
			this.bufferedReadLineSupplier = Objects.requireNonNull(bufferedReadLineSupplier);
			return this;
		}

		public ExecSoftFoundFactoryBuilder withExecutorFactory(ExecutorFactory executorFactory) {
			this.executorFactory = executorFactory;
			return this;
		}

		public ExecSoftFoundFactory build() {
			if(build) {
				throw new IllegalStateException("Already call");
			}
			if(parserFactory == null) {
				throw new IllegalStateException("ParserFactory is missing");
			}
			ExecutorFactory execFact = executorFactory != null ? executorFactory : getDefaultExecutorFactory();
			build = true;
			return new ExecSoftFoundFactory(execFact, parameters, parserFactory, bufferedReadLineSupplier);
		}

		// **********************************************************

		private ExecutorFactory getDefaultExecutorFactory() {
			return (file, parser, readLine) -> {
				FMVExecutor fmvExecutor = FMVExecutor.with(file.getParentFile())
						.out(getOutReadLine(readLine, parser::readLineOut))
						.err(getErrReadLine(readLine, parser::readLineErr))
						.charset(charset)
						.lookReader(lookReader)
						.build();
				fmvExecutor.setTimeOut(10_000); // default: 10 seconds
				applyCustomizeExecutor(fmvExecutor);
				return fmvExecutor;
			};
		}

	}

	// ------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	@FunctionalInterface
	public interface ParserFactory {

		/**
		 * @param file
		 * @param softPolicy
		 * @return
		 */
		Parser create(File file, SoftPolicy softPolicy);
	}

	// ------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public interface Parser {

		/**
		 * @param line
		 */
		void readLine(String line);

		/**
		 * @param line
		 */
		default void readLineOut(String line) {
			readLine(line);
		}

		/**
		 * @param line
		 */
		default void readLineErr(String line) {
			readLine(line);
		}

		/**
		 * @param cmdLineStr
		 * @param exitValue
		 * @return
		 * @throws IOException
		 */
		SoftFound closeAndParse(String cmdLineStr, int exitValue) throws IOException;

		/**
		 * @param ioException
		 * @param cmdLineStr
		 * @param allLines
		 * @return
		 * @throws IOException
		 */
		default SoftFound closeAndParse(IOException ioException, String cmdLineStr, List<String> allLines) throws IOException {
			String msg = cmdLineStr + SystemUtils.LINE_SEPARATOR + allLines.stream().collect(Collectors.joining(SystemUtils.LINE_SEPARATOR));
			throw new IOException(msg, ioException);
		}

		/**
		 * @param executeException
		 * @param cmdLineStr
		 * @param allLines
		 * @return
		 * @throws IOException
		 */
		default SoftFound closeAndParse(ExecuteException executeException, String cmdLineStr, List<String> allLines) throws ExecuteException {
			String msg = cmdLineStr + SystemUtils.LINE_SEPARATOR + allLines.stream().collect(Collectors.joining(SystemUtils.LINE_SEPARATOR));
			throw new ExecuteException(msg, executeException.getExitValue(), executeException);
		}

	}

	// ------------------------------------------------------------

	/**
	 * @author fagu
	 */
	@FunctionalInterface
	public interface ParseVersion {

		/**
		 * @param line
		 * @return
		 */
		Version readLineAndParse(String line);
	}

	// ------------------------------------------------------------

	/**
	 * @author fagu
	 */
	@FunctionalInterface
	public interface ParseVersionDate {

		/**
		 * @param line
		 * @return
		 */
		VersionDate readLineAndParse(String line);
	}

	// ------------------------------------------------------------

	/**
	 * @author fagu
	 */
	public static class VersionDate {

		private final Version version;

		private final Date date;

		public VersionDate(Version version, Date date) {
			this.version = version;
			this.date = date;
		}

		public VersionDate(Version version) {
			this(version, null);
		}

		public VersionDate(Date date) {
			this(null, date);
		}

		Optional<Date> getDate() {
			return Optional.ofNullable(date);
		}

		Optional<Version> getVersion() {
			return Optional.ofNullable(version);
		}

	}

	// ------------------------------------------------------------
	/**
	 * @author fagu
	 */
	@FunctionalInterface
	public interface ExecutorFactory {

		/**
		 * @param file
		 * @param parser
		 * @param readLine
		 * @return
		 */
		FMVExecutor create(File file, Parser parser, ReadLine readLine);
	}

	// ------------------------------------------------------------

	private final List<String> parameters;

	private final ExecutorFactory executorFactory;

	private final ParserFactory parserFactory;

	private final Supplier<List<String>> bufferedReadLineSupplier;

	/**
	 * @param executorFactory
	 * @param parameters
	 * @param parserFactory
	 * @param bufferedReadLineSupplier
	 */
	private ExecSoftFoundFactory(ExecutorFactory executorFactory, List<String> parameters, ParserFactory parserFactory,
			Supplier<List<String>> bufferedReadLineSupplier) {
		this.executorFactory = Objects.requireNonNull(executorFactory);
		this.parameters = parameters;
		this.parserFactory = parserFactory;
		this.bufferedReadLineSupplier = bufferedReadLineSupplier;
	}

	/**
	 * @param softProvider
	 * @return
	 */
	public static ExecSoftFoundFactoryPrepare forProvider(SoftProvider softProvider) {
		return new ExecSoftFoundFactoryPrepare(softProvider);
	}

	/**
	 * @return
	 */
	public ParserFactory getParserFactory() {
		return parserFactory;
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftFoundFactory#create(java.io.File, Locator, SoftPolicy)
	 */
	@Override
	public final SoftFound create(File file, Locator locator, SoftPolicy softPolicy) throws IOException {
		Parser parser = parserFactory.create(file, softPolicy);
		CommandLine commandLine = FMVCommandLine.create(file, parameters);
		String cmdLineStr = CommandLineUtils.toLine(commandLine);

		List<String> readLineList = Objects.requireNonNull(bufferedReadLineSupplier.get());
		BufferedReadLine bufferedReadLine = new BufferedReadLine(readLineList);

		FMVExecutor executor = executorFactory.create(file, parser, bufferedReadLine);

		try {
			int exitValue = executor.execute(commandLine);
			SoftFound softFound = parser.closeAndParse(cmdLineStr, exitValue);
			if(locator != null && softFound != null) {
				softFound.setLocalizedBy(locator.toString());
			}
			return softFound;
		} catch(IOException e) {
			return parser.closeAndParse(e, cmdLineStr, readLineList);
		}
	}

}
