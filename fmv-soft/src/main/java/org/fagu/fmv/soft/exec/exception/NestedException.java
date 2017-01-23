package org.fagu.fmv.soft.exec.exception;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fagu.fmv.soft.FMVExecuteException;


/**
 * @author f.agu
 */
public class NestedException {

	private final Exception exception;

	private List<String> msgLines;

	/**
	 * @param exception
	 */
	public NestedException(Exception exception) {
		this.exception = exception;
	}

	/**
	 * @return
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @return
	 */
	public boolean isFMVExecutorException() {
		return exception instanceof FMVExecuteException;
	}

	/**
	 * @return
	 */
	public List<String> messageToLines() {
		if(msgLines == null) {
			List<String> list = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new StringReader(exception.getMessage()))) {
				String line = null;
				while((line = reader.readLine()) != null) {
					list.add(line);
				}
			} catch(IOException e) {
				// never append
			}
			msgLines = Collections.unmodifiableList(list);
		}
		return msgLines;
	}

	/**
	 * @param strToFind
	 * @return
	 */
	public boolean contains(String strToFind) {
		return exception.getMessage().contains(strToFind);
	}
}
