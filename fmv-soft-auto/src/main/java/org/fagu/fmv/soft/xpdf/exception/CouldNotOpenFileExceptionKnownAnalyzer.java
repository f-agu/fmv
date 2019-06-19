package org.fagu.fmv.soft.xpdf.exception;

/*-
 * #%L
 * fmv-ffmpeg
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

/**
 * @author f.agu
 */
public class CouldNotOpenFileExceptionKnownAnalyzer extends XpdfExceptionKnownAnalyzer {

	public CouldNotOpenFileExceptionKnownAnalyzer() {
		super("Couldn't open file", "I/O Error: Couldn't open file");
	}

}
