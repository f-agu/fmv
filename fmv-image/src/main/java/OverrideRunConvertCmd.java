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
import java.lang.ProcessBuilder.Redirect;
import java.util.LinkedList;

import org.im4java.core.ConvertCmd;
import org.im4java.process.ProcessStarter;


/**
 * @author f.agu
 */
public class OverrideRunConvertCmd extends ConvertCmd {

	/**
	 *
	 */
	public OverrideRunConvertCmd() {}

	/**
	 * @param useGM
	 */
	public OverrideRunConvertCmd(boolean useGM) {
		super(useGM);
	}

	// ***********************************

	/**
	 * @see org.im4java.process.ProcessStarter#run(java.util.LinkedList)
	 */
	@Override
	protected int run(LinkedList<String> pArgs) throws IOException, InterruptedException, Exception {
		String cmd = pArgs.getFirst();
		cmd = searchForCmd(cmd, ProcessStarter.getGlobalSearchPath());
		pArgs.set(0, cmd);

		ProcessBuilder builder = new ProcessBuilder(pArgs);
		builder.redirectInput(Redirect.PIPE);
		builder.redirectOutput(Redirect.PIPE);
		Process pr = builder.start();
		// int rc = waitForProcess(pr);
		// finished(rc);
		// return rc;
		return 0;
	}

}
