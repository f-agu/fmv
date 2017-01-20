package org.fagu.fmv.soft.exec.exception;

/**
 * @author f.agu
 */
@FunctionalInterface
public interface ExceptionKnownAnalyzer {

	/**
	 * @param nestedException
	 * @return
	 */
	ExceptionKnown anaylze(NestedException nestedException);

}
