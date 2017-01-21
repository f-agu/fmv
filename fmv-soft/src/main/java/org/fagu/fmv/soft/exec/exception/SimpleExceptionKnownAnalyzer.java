package org.fagu.fmv.soft.exec.exception;

import java.util.Objects;


/**
 * @author fagu
 */
public class SimpleExceptionKnownAnalyzer implements ExceptionKnownAnalyzer {

	private final String title;

	private final String strToFind;

	/**
	 * @param title
	 * @param strToFind
	 */
	public SimpleExceptionKnownAnalyzer(String title, String strToFind) {
		this.title = Objects.requireNonNull(title);
		this.strToFind = Objects.requireNonNull(strToFind);
	}

	/**
	 * @see org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer#anaylze(org.fagu.fmv.soft.exec.exception.NestedException)
	 */
	@Override
	public ExceptionKnown anaylze(NestedException nestedException) {
		if(nestedException.contains(strToFind)) {
			return new ExceptionKnown(title);
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return
	 */
	public String getStrToFind() {
		return strToFind;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return title;
	}

}
