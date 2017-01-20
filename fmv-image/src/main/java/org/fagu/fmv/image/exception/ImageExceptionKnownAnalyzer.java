package org.fagu.fmv.image.exception;

import org.fagu.fmv.soft.exec.exception.SimpleExceptionKnownAnalyzer;


/**
 * @author f.agu
 */
public abstract class ImageExceptionKnownAnalyzer extends SimpleExceptionKnownAnalyzer {

	/**
	 * @param title
	 * @param strToFind
	 */
	public ImageExceptionKnownAnalyzer(String title, String strToFind) {
		super(title, strToFind);
	}

}
