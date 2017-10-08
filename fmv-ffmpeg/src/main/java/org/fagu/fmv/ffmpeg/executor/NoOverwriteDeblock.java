package org.fagu.fmv.ffmpeg.executor;

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
