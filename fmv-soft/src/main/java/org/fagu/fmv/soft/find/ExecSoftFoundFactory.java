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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.exec.BufferedReadLine;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.ExecHelper;
import org.fagu.fmv.soft.exec.FMVCommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author f.agu
 */
public class ExecSoftFoundFactory implements SoftFoundFactory {

	// ------------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class ExecSoftFoundFactoryBuilder extends ExecHelper<ExecSoftFoundFactoryBuilder> {

		private final ExecSoftFoundFactory execSoftInfoFactory;

		private boolean build;

		private List<String> readLineList;

		private ExecSoftFoundFactoryBuilder(ExecSoftFoundFactory execSoftInfoFactory) {
			this.execSoftInfoFactory = execSoftInfoFactory;
			execSoftInfoFactory.builder = this;
		}

		public ExecSoftFoundFactoryBuilder parseFactory(ParserFactory parserFactory) {
			execSoftInfoFactory.parserFactory = parserFactory;
			return this;
		}

		public ExecSoftFoundFactory build() {
			build = true;
			if( ! build) {
				throw new IllegalStateException("Already call");
			}
			if(execSoftInfoFactory.parserFactory == null) {
				throw new IllegalStateException("ParserFactory is missing");
			}
			return execSoftInfoFactory;
		}

		// **********************************************************

		private FMVExecutor createExecutor(File file, Parser parser) {
			readLineList = new ArrayList<>();
			BufferedReadLine bufferedReadLine = new BufferedReadLine(readLineList);

			FMVExecutor fmvExecutor = FMVExecutor.create(file.getParentFile(), getOutReadLine(bufferedReadLine, parser::readLineOut), getErrReadLine(
					bufferedReadLine, parser::readLineErr));
			fmvExecutor.setTimeOut(10_000); // default: 10 seconds
			applyCustomizeExecutor(fmvExecutor);
			return fmvExecutor;
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
		 * @return
		 */
		Parser create(File file);
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

	private final List<String> parameters;

	private ParserFactory parserFactory;

	private ExecSoftFoundFactoryBuilder builder;

	/**
	 * @param parameters
	 */
	private ExecSoftFoundFactory(String... parameters) {
		this.parameters = Collections.unmodifiableList(Arrays.asList(parameters));
	}

	/**
	 * @param parameters
	 * @return
	 */
	public static ExecSoftFoundFactoryBuilder withParameters(String... parameters) {
		return new ExecSoftFoundFactoryBuilder(new ExecSoftFoundFactory(parameters));
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftFoundFactory#create(java.io.File)
	 */
	@Override
	public final SoftFound create(File file) throws ExecutionException, IOException {
		Parser parser = parserFactory.create(file);
		CommandLine commandLine = FMVCommandLine.create(file, parameters);
		String cmdLineStr = CommandLineUtils.toLine(commandLine);

		FMVExecutor executor = builder.createExecutor(file, parser);

		try {
			int exitValue = executor.execute(commandLine);
			return parser.closeAndParse(cmdLineStr, exitValue);
		} catch(ExecuteException e) {
			return parser.closeAndParse(e, cmdLineStr, builder.readLineList);
		} catch(IOException e) {
			return parser.closeAndParse(e, cmdLineStr, builder.readLineList);
		}
	}

}
