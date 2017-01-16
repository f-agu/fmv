package org.fagu.fmv.image.soft;

/*-
 * #%L
 * fmv-image
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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.im4java.process.ProcessStarter;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 nov. 2016 15:29:00
 */
class OverrideLog {

	/**
	 * 
	 */
	private OverrideLog() {}

	/**
	 * @param logger
	 * @param pArgs
	 * @return
	 */
	static void log(Consumer<String> logger, LinkedList<String> pArgs) {
		List<String> cmds = new ArrayList<>(pArgs);
		cmds.set(0, ProcessStarter.getGlobalSearchPath() + File.separator + cmds.get(0));
		logger.accept(CommandLineUtils.toLine(cmds));
	}

}
