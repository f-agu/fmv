package org.fagu.fmv.soft.exec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;


/**
 * @author f.agu
 */
public class ExecHelper<T> {

	protected boolean debug;

	private Consumer<String> outDebugConsumer;

	private Consumer<String> errDebugConsumer;

	protected OutputStream out;

	protected OutputStream err;

	protected InputStream input;

	protected final List<ReadLine> commonReadLines;

	protected final List<ReadLine> outReadLines;

	protected final List<ReadLine> errReadLines;

	protected ExecuteStreamHandler executeStreamHandler;

	protected List<Consumer<FMVExecutor>> customizeExecutors;

	/**
	 *
	 */
	public ExecHelper() {
		commonReadLines = new ArrayList<>();
		outReadLines = new ArrayList<>();
		errReadLines = new ArrayList<>();
		customizeExecutors = new ArrayList<>();
		outDebugConsumer = line -> System.out.println("OUT  " + line);
		errDebugConsumer = line -> System.out.println("ERR  " + line);
	}

	/**
	 * @return
	 */
	public T debug() {
		return debug(true);
	}

	/**
	 * @param debug
	 * @return
	 */
	public T debug(boolean debug) {
		return debug(debug, null, null);
	}

	/**
	 * @param debug
	 * @param outDebugConsumer
	 * @param errDebugConsumer
	 * @return
	 */
	public T debug(boolean debug, Consumer<String> outDebugConsumer, Consumer<String> errDebugConsumer) {
		this.debug = debug;
		if(outDebugConsumer != null) {
			this.outDebugConsumer = outDebugConsumer;
		}
		if(errDebugConsumer != null) {
			this.errDebugConsumer = errDebugConsumer;
		}
		return getThis();
	}

	/**
	 * @param readLine
	 * @return
	 */
	public T addCommonReadLine(ReadLine readLine) {
		commonReadLines.add(Objects.requireNonNull(readLine));
		return getThis();
	}

	/**
	 * @param readLine
	 * @return
	 */
	public T addOutReadLine(ReadLine readLine) {
		outReadLines.add(Objects.requireNonNull(readLine));
		return getThis();
	}

	/**
	 * @param readLine
	 * @return
	 */
	public T addErrReadLine(ReadLine readLine) {
		errReadLines.add(Objects.requireNonNull(readLine));
		return getThis();
	}

	/**
	 * @param consumer
	 * @return
	 */
	public T customizeExecutor(Consumer<FMVExecutor> consumer) {
		if(consumer != null) {
			customizeExecutors.add(consumer);
		}
		return getThis();
	}

	/**
	 * @param executeStreamHandler
	 * @return
	 */
	public T streamHandler(ExecuteStreamHandler executeStreamHandler) {
		this.executeStreamHandler = executeStreamHandler;
		return getThis();
	}

	/**
	 * @param input
	 * @return
	 */
	public T input(InputStream input) {
		this.input = input;
		return getThis();
	}

	/**
	 * @param out
	 * @return
	 */
	public T out(OutputStream out) {
		this.out = out;
		return getThis();
	}

	/**
	 * @param err
	 * @return
	 */
	public T err(OutputStream err) {
		this.err = err;
		return getThis();
	}

	// *******************************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ReadLine getOutReadLine(ReadLine... readLines) {
		List<ReadLine> lines = new ArrayList<>();
		if(debug) {
			lines.add(outDebugConsumer::accept);
		}
		populateReadLine(lines, outReadLines, Arrays.asList(readLines));
		return MultiReadLine.createWith(lines);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ReadLine getErrReadLine(ReadLine... readLines) {
		List<ReadLine> lines = new ArrayList<>();
		if(debug) {
			lines.add(errDebugConsumer::accept);
		}
		populateReadLine(lines, errReadLines, Arrays.asList(readLines));
		return MultiReadLine.createWith(lines);
	}

	/**
	 * @param toAddLines
	 * @param externLines
	 */
	protected void populateReadLine(List<ReadLine> toAddLines, @SuppressWarnings("unchecked") List<ReadLine>... externLines) {
		toAddLines.addAll(commonReadLines);

		if(externLines != null) {
			for(List<ReadLine> readLines : externLines) {
				readLines.stream().filter(Objects::nonNull).forEach(toAddLines::add);
			}
		}
	}

	/**
	 * @param fmvExecutor
	 */
	protected void applyCustomizeExecutor(FMVExecutor fmvExecutor) {
		customizeExecutors.forEach(c -> c.accept(fmvExecutor));
	}

	/**
	 * @throws IOException
	 */
	protected void checkStreamHandler() throws IOException {
		if(executeStreamHandler != null && ( ! commonReadLines.isEmpty() || ! outReadLines.isEmpty() || ! errReadLines.isEmpty()) && (input != null
				|| out != null || err != null)) {
			throw new IOException("Choose between a StreamHandler and a ReadLine and an input/out/err.");
		}
	}

	/**
	 * @param workingFolder
	 * @param defaultReaDLine
	 * @return
	 * @throws IOException
	 */
	protected FMVExecutor createFMVExecutor(File workingFolder, ReadLine defaultReaDLine) throws IOException {
		checkStreamHandler();

		// executeStreamHandler
		if(executeStreamHandler != null) {
			return FMVExecutor.create(workingFolder, executeStreamHandler);
		}

		// input/out/err
		if(input != null || out != null || err != null) { // input & whatever
			ReadLineOutputStream outRL = new ReadLineOutputStream(out, defaultReaDLine);
			ReadLineOutputStream errRL = new ReadLineOutputStream(err, defaultReaDLine);
			ExecuteStreamHandler customExecuteStreamHandler = new PumpStreamHandler(outRL, errRL, input);
			return FMVExecutor.create(workingFolder, customExecuteStreamHandler);
		}

		// ReadLine
		return FMVExecutor.create(workingFolder, getOutReadLine(defaultReaDLine), getErrReadLine(defaultReaDLine));
	}

	// *******************************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T getThis() {
		return (T)this;
	}

}
