package org.fagu.fmv.soft.xpdf.exception;

import java.util.List;
import java.util.Optional;

import org.fagu.fmv.soft.exec.exception.ExceptionKnown;
import org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzers;
import org.fagu.fmv.soft.exec.exception.SimpleExceptionKnownAnalyzer;


/**
 * @author f.agu
 */
public abstract class XpdfExceptionKnownAnalyzer extends SimpleExceptionKnownAnalyzer {

	/**
	 * @param title
	 * @param strToFind
	 */
	public XpdfExceptionKnownAnalyzer(String title, String strToFind) {
		super(title, strToFind);
	}

	/**
	 * @return
	 */
	public static List<XpdfExceptionKnownAnalyzer> getAnalyzers() {
		return ExceptionKnownAnalyzers.getExceptionKnownAnalyzers(XpdfExceptionKnownAnalyzer.class);
	}

	/**
	 * @param e
	 * @return
	 */
	public static Optional<ExceptionKnown> getKnown(Exception e) {
		return ExceptionKnownAnalyzers.getKnown(XpdfExceptionKnownAnalyzer.class, e);
	}

}
