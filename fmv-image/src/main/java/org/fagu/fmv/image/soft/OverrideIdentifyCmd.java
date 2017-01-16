package org.fagu.fmv.image.soft;

import java.io.FileNotFoundException;

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
import java.util.function.Consumer;

import org.im4java.core.IdentifyCmd;


/**
 * @author f.agu
 */
public class OverrideIdentifyCmd extends IdentifyCmd {

	private final Consumer<String> logger;

	/**
	 * @param logger
	 */
	public OverrideIdentifyCmd(Consumer<String> logger) {
		this.logger = logger;
	}

	/**
	 * @see org.im4java.process.ProcessStarter#searchForCmd(java.lang.String, java.lang.String)
	 */
	@Override
	public String searchForCmd(String pCmd, String pPath) throws IOException, FileNotFoundException {
		return OverrideSearchForCmd.searchForCmd(pCmd, pPath);
	}

	// ***********************************

	/**
	 * @see org.im4java.process.ProcessStarter#run(java.util.LinkedList)
	 */
	@Override
	protected int run(LinkedList<String> pArgs) throws IOException, InterruptedException, Exception {
		if(logger != null) {
			OverrideLog.log(logger, pArgs);
		}
		return super.run(pArgs);
	}

}
