package org.fagu.fmv.im.exception;

/*-
 * #%L
 * fmv-imagemagick
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
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
			return new ExceptionKnown(nestedException, line);
		}
		return null;
	}

}
