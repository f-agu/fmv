package org.fagu.fmv.im.exception;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.exec.exception.ExceptionKnown;
import org.fagu.fmv.soft.exec.exception.NestedException;


/**
 * @author f.agu
 */
public class NoDecoderExceptionKnownAnalyzer extends IMExceptionKnownAnalyzer {

	/**
	 * 
	 */
	public NoDecoderExceptionKnownAnalyzer() {
		super("No decoder defined", "no decode delegate for this image format");
	}

	/**
	 * @see org.fagu.fmv.soft.exec.exception.ExceptionKnownAnalyzer#anaylze(org.fagu.fmv.soft.exec.exception.NestedException)
	 */
	@Override
	public ExceptionKnown anaylze(NestedException nestedException) {
		Optional<String> findFirst = nestedException.messageToLines().stream().filter(l -> l.contains(getStrToFind())).findFirst();
		if(findFirst.isPresent()) {
			String line = StringUtils.substringBefore(findFirst.get(), " @ error").replace('`', '\'');
			return new ExceptionKnown(line);
		}
		return null;
	}

}
