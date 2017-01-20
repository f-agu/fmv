package org.fagu.fmv.soft.exec.exception;

import java.util.Objects;


/**
 * @author f.agu
 */
public class FindStringSPIExceptionKnown implements ExceptionKnown {

	private final String title;

	private final String strToFind;

	/**
	 * @param title
	 * @param strToFind
	 */
	public FindStringSPIExceptionKnown(String title, String strToFind) {
		this.title = Objects.requireNonNull(title);
		this.strToFind = Objects.requireNonNull(strToFind);
	}

	/**
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(NestedException t) {
		return t.messageToLines().stream().anyMatch(l -> l.contains(strToFind));
	}

	/**
	 * @see org.fagu.fmv.soft.exec.exception.ExceptionKnown#title()
	 */
	@Override
	public String title() {
		return title;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return title;
	}

}
