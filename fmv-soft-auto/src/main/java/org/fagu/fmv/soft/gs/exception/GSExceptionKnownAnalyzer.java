package org.fagu.fmv.soft.gs.exception;

import java.util.List;
import java.util.Optional;

import org.fagu.fmv.soft.exec.exception.ExceptionKnown;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzers;
import org.fagu.fmv.soft.exec.exception.SimpleExceptionKnownAnalyzer;


/**
 * @author f.agu
 */
public abstract class GSExceptionKnownAnalyzer extends SimpleExceptionKnownAnalyzer {

	/**
	 * @param title
	 * @param strToFind
	 */
	public GSExceptionKnownAnalyzer(String title, String strToFind) {
		super(title, strToFind);
	}

	/**
	 * @return
	 */
	public static List<GSExceptionKnownAnalyzer> getAnalyzers() {
		return ExceptionKnownAnalyzers.getExceptionKnownAnalyzers(GSExceptionKnownAnalyzer.class);
	}

	/**
	 * @param e
	 * @return
	 */
	public static Optional<ExceptionKnown> getKnown(Exception e) {
		return ExceptionKnownAnalyzers.getKnown(GSExceptionKnownAnalyzer.class, e);
	}

}
