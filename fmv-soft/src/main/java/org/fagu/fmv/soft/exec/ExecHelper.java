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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * @author f.agu
 */
public class ExecHelper<T> {

	protected boolean debug;

	protected final List<ReadLine> commonReadLines;

	protected final List<ReadLine> outReadLines;

	protected final List<ReadLine> errReadLines;

	protected Consumer<FMVExecutor> customizeExecutor;

	/**
	 *
	 */
	public ExecHelper() {
		commonReadLines = new ArrayList<>();
		outReadLines = new ArrayList<>();
		errReadLines = new ArrayList<>();
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
		this.debug = debug;
		return getThis();
	}

	/**
	 * @param readLine
	 * @return
	 */
	public T addCommonReadLine(ReadLine readLine) {
		this.commonReadLines.add(Objects.requireNonNull(readLine));
		return getThis();
	}

	/**
	 * @param readLine
	 * @return
	 */
	public T addOutReadLine(ReadLine readLine) {
		this.outReadLines.add(Objects.requireNonNull(readLine));
		return getThis();
	}

	/**
	 * @param readLine
	 * @return
	 */
	public T addErrReadLine(ReadLine readLine) {
		this.errReadLines.add(Objects.requireNonNull(readLine));
		return getThis();
	}

	/**
	 * @param consumer
	 * @return
	 */
	public T customizeExecutor(Consumer<FMVExecutor> consumer) {
		this.customizeExecutor = consumer;
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
			lines.add(line -> System.out.println("OUT  " + line));
		}
		populateReadLine(lines, outReadLines, Arrays.asList(readLines));
		return new MultiReadLine(lines);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ReadLine getErrReadLine(ReadLine... readLines) {
		List<ReadLine> lines = new ArrayList<>();
		if(debug) {
			lines.add(line -> System.out.println("ERR  " + line));
		}
		populateReadLine(lines, errReadLines, Arrays.asList(readLines));
		return new MultiReadLine(lines);
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
		if(customizeExecutor != null) {
			customizeExecutor.accept(fmvExecutor);
		}
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
