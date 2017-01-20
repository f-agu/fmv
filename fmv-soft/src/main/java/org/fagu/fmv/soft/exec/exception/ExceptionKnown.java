package org.fagu.fmv.soft.exec.exception;

import java.util.function.Predicate;


/**
 * @author f.agu
 */
public interface ExceptionKnown extends Predicate<NestedException> {

	/**
	 * @return
	 */
	String title();

}
