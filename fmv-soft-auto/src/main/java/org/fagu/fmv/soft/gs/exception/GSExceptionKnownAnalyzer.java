package org.fagu.fmv.soft.gs.exception;

/*-
 * #%L
 * fmv-soft-auto
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
