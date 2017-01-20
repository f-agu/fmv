package org.fagu.fmv.image.exception;

import org.fagu.fmv.soft.exec.exception.FindStringSPIExceptionKnown;


/**
 * @author f.agu
 */
public class ImageExceptionKnown extends FindStringSPIExceptionKnown {

	/**
	 * @param title
	 * @param strToFind
	 */
	public ImageExceptionKnown(String title, String strToFind) {
		super(title, strToFind);
	}

}
