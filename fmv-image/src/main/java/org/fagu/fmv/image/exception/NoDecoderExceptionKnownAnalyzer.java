package org.fagu.fmv.image.exception;

/**
 * @author f.agu
 */
public class NoDecoderExceptionKnownAnalyzer extends ImageExceptionKnownAnalyzer {

	/**
	 * 
	 */
	public NoDecoderExceptionKnownAnalyzer() {
		super("No decoder defined", "no decode delegate for this image format");
	}

}
