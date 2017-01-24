package org.fagu.fmv.im.exception;

import java.util.List;
import java.util.Optional;

import org.fagu.fmv.soft.exec.exception.ExceptionKnown;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzers;
import org.fagu.fmv.soft.exec.exception.SimpleExceptionKnownAnalyzer;


/**
 * @author f.agu
 */
public abstract class IMExceptionKnownAnalyzer extends SimpleExceptionKnownAnalyzer {

	/**
	 * @param title
	 * @param strToFind
	 */
	public IMExceptionKnownAnalyzer(String title, String strToFind) {
		super(title, strToFind);
	}

	/**
	 * @return
	 */
	public static List<IMExceptionKnownAnalyzer> getAnalyzers() {
		return ExceptionKnownAnalyzers.getExceptionKnownAnalyzers(IMExceptionKnownAnalyzer.class);
	}

	/**
	 * @param e
	 * @return
	 */
	public static Optional<ExceptionKnown> getKnown(Exception e) {
		return ExceptionKnownAnalyzers.getKnown(IMExceptionKnownAnalyzer.class, e);
	}

}
