package org.fagu.fmv.ffmpeg.executor;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.io.IOException;
import java.util.List;

import org.fagu.fmv.ffmpeg.exception.FFExceptionKnownAnalyzer;
import org.fagu.fmv.soft.exec.LookReader;
import org.fagu.fmv.soft.exec.ProcessOperator;
import org.fagu.fmv.soft.exec.exception.ExceptionKnown;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownConsumer;
import org.fagu.fmv.soft.exec.exception.NestedException;
import org.fagu.fmv.soft.exec.exception.ProcessBlockedException;


/**
 * @author fagu
 */
public class NoOverwriteDeblock implements LookReader, ProcessOperator, ExceptionKnownConsumer, FFExceptionKnownAnalyzer {

	private Process process;

	@Override
	public Process operate(Process process) {
		this.process = process;
		return process;
	}

	@Override
	public boolean look(String line) throws IOException {
		if(isBlocked(line)) {
			process.destroy();
			throw new ProcessBlockedException("By a question: " + line);
		}
		return ! line.startsWith("frame= ");
	}

	@Override
	public ExceptionKnown anaylze(NestedException nestedException) {
		String lastLine = getLineIfBlocked(nestedException);
		if(lastLine != null) {
			return new ExceptionKnown(nestedException, "Process blocked by: " + lastLine);
		}
		return null;
	}

	@Override
	public void accept(ExceptionKnown exceptionKnown) throws IOException {
		NestedException nestedException = exceptionKnown.getNestedException();
		String lastLine = getLineIfBlocked(nestedException);
		if(lastLine != null) {
			throw new ProcessBlockedException(lastLine);
		}
		throw nestedException.getIOException();
	}

	// *************************************************

	private String getLineIfBlocked(NestedException nestedException) {
		List<String> lines = nestedException.messageToLines();
		if(lines.isEmpty()) {
			return null;
		}
		String lastLine = lines.get(lines.size() - 1);
		return isBlocked(lastLine) ? lastLine : null;
	}

	private boolean isBlocked(String line) {
		return line.startsWith("File ") && line.contains(" already exists. Overwrite ? [y/N]");
	}

}
