package org.fagu.fmv.mymedia.reduce;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2016 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.im4java.core.ConvertCmd;


/**
 * @author f.agu
 */
public class OverrideLogConvertCmd extends ConvertCmd {

	private final Logger logger;

	/**
	 * @param logger
	 */
	public OverrideLogConvertCmd(Logger logger) {
		this.logger = Objects.requireNonNull(logger);
	}

	/**
	 * @param logger
	 * @param useGM
	 */
	public OverrideLogConvertCmd(Logger logger, boolean useGM) {
		super(useGM);
		this.logger = Objects.requireNonNull(logger);
	}

	// ***********************************

	/**
	 * @see org.im4java.process.ProcessStarter#run(java.util.LinkedList)
	 */
	@Override
	protected int run(LinkedList<String> pArgs) throws IOException, InterruptedException, Exception {
		logger.log("Exec: " + CommandLineUtils.toLine(pArgs));
		return super.run(pArgs);
	}

}
