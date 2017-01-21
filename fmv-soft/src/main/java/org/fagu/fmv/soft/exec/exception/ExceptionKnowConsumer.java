package org.fagu.fmv.soft.exec.exception;

import java.io.IOException;


/**
 * @author fagu
 */
@FunctionalInterface
public interface ExceptionKnowConsumer {

	/**
	 * @param exceptionKnown
	 * @throws IOException
	 */
	void accept(ExceptionKnown exceptionKnown) throws IOException;
}
