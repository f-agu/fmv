package org.fagu.fmv.soft.exec;

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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.exec.ExecuteStreamHandler;


/**
 * @author f.agu
 */
public class ExecHelper<T extends ExecHelper<?>> {

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

	protected LookReader lookReader;

	protected List<Consumer<FMVExecutor>> customizeExecutors;

	protected Charset charset;

	public ExecHelper() {
		commonReadLines = new ArrayList<>();
		outReadLines = new ArrayList<>();
		errReadLines = new ArrayList<>();
		customizeExecutors = new ArrayList<>();
		outDebugConsumer = line -> System.out.println("OUT  " + line);
		errDebugConsumer = line -> System.out.println("ERR  " + line);
	}

	public T debug() {
		return debug(true);
	}

	public T debug(boolean debug) {
		return debug(debug, null, null);
	}

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

	public T addCommonReadLine(ReadLine readLine) {
		if(readLine != null) {
			commonReadLines.add(readLine);
		}
		return getThis();
	}

	public T addOutReadLine(ReadLine readLine) {
		if(readLine != null) {
			outReadLines.add(readLine);
		}
		return getThis();
	}

	public T addErrReadLine(ReadLine readLine) {
		if(readLine != null) {
			errReadLines.add(readLine);
		}
		return getThis();
	}

	public T customizeExecutor(Consumer<FMVExecutor> consumer) {
		if(consumer != null) {
			customizeExecutors.add(consumer);
		}
		return getThis();
	}

	public T streamHandler(ExecuteStreamHandler executeStreamHandler) {
		this.executeStreamHandler = executeStreamHandler;
		return getThis();
	}

	public T lookReader(LookReader lookReader) {
		this.lookReader = lookReader;
		return getThis();
	}

	public T input(InputStream input) {
		this.input = input;
		return getThis();
	}

	public T output(OutputStream out) {
		this.out = out;
		return getThis();
	}

	@Deprecated
	public T out(OutputStream out) {
		return output(out);
	}

	public T err(OutputStream err) {
		this.err = err;
		return getThis();
	}

	public T charset(Charset charset) {
		this.charset = charset;
		return getThis();
	}

	public T exitValue(int value) {
		return customizeExecutor(e -> e.setExitValue(value));
	}

	public T exitValues(int... values) {
		return customizeExecutor(e -> e.setExitValues(values));
	}

	public T timeOut(long timeOutMilliSeconds) {
		return customizeExecutor(e -> e.setTimeOut(timeOutMilliSeconds));
	}

	public T workingDirectory(File workingDirectory) {
		return customizeExecutor(e -> e.setWorkingDirectory(workingDirectory));
	}

	public Charset getCharset() {
		return charset;
	}

	// *******************************************************

	protected ReadLine getOutReadLine(ReadLine... readLines) {
		List<ReadLine> lines = new ArrayList<>();
		if(debug) {
			lines.add(outDebugConsumer::accept);
		}
		populateReadLine(lines, Arrays.asList(outReadLines, Arrays.asList(readLines)));
		return MultiReadLine.createWith(lines);
	}

	protected ReadLine getErrReadLine(ReadLine... readLines) {
		List<ReadLine> lines = new ArrayList<>();
		if(debug) {
			lines.add(errDebugConsumer::accept);
		}
		populateReadLine(lines, Arrays.asList(errReadLines, Arrays.asList(readLines)));
		return MultiReadLine.createWith(lines);
	}

	protected void populateReadLine(List<ReadLine> toAddLines, List<List<ReadLine>> externLines) {
		toAddLines.addAll(commonReadLines);

		if(externLines != null) {
			for(List<ReadLine> readLines : externLines) {
				readLines.stream()
						.filter(Objects::nonNull)
						.forEach(toAddLines::add);
			}
		}
	}

	protected void applyCustomizeExecutor(FMVExecutor fmvExecutor) {
		customizeExecutors.forEach(c -> c.accept(fmvExecutor));
	}

	protected void checkStreamHandler() throws IOException {
		if(executeStreamHandler != null && ( ! commonReadLines.isEmpty() || ! outReadLines.isEmpty() || ! errReadLines.isEmpty()) && (input != null
				|| out != null || err != null)) {
			throw new IOException("Choose between a StreamHandler and a ReadLine and an input/out/err.");
		}
	}

	protected FMVExecutor createFMVExecutor(File workingFolder, ReadLine defaultReadLine) throws IOException {
		checkStreamHandler();

		// executeStreamHandler
		if(executeStreamHandler != null) {
			return FMVExecutor.with(workingFolder)
					.executeStreamHandler(executeStreamHandler)
					.build();
		}

		// input/out/err
		if(input != null || out != null || err != null) { // input & whatever
			ReadLineOutputStream outRL = new ReadLineOutputStream(out, getOutReadLine(defaultReadLine), charset);
			ReadLineOutputStream errRL = new ReadLineOutputStream(err, getErrReadLine(defaultReadLine), charset);
			ExecuteStreamHandler customExecuteStreamHandler = new MyPumpStreamHandler(outRL, errRL, input);

			return FMVExecutor.with(workingFolder)
					.executeStreamHandler(customExecuteStreamHandler)
					.build();
		}

		// ReadLine
		return FMVExecutor.with(workingFolder)
				.out(getOutReadLine(defaultReadLine))
				.err(getErrReadLine(defaultReadLine))
				.charset(charset)
				.lookReader(lookReader)
				.build();
	}

	// *******************************************************

	@SuppressWarnings("unchecked")
	private T getThis() {
		return (T)this;
	}

}
