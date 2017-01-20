package org.fagu.fmv.image.exception;

/**
 * @author f.agu
 */
public class NoDecoderExceptionKnown extends ImageExceptionKnown {

	/**
	 * 
	 */
	public NoDecoderExceptionKnown() {
		super("No decoder defined", "no decode delegate for this image format");
	}

}
