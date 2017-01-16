package org.fagu.fmv.image.soft;

/*-
 * #%L
 * fmv-image
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * @author Oodrive
 * @author f.agu
 * @created 12 janv. 2017 12:57:43
 */
class OverrideSearchForCmd {

	/**
	 * 
	 */
	private OverrideSearchForCmd() {}

	/**
	 * @param pCmd
	 * @param pPath
	 * @return
	 * @throws IOException
	 */
	static String searchForCmd(String pCmd, String pPath)
			throws IOException {

		// check is pCmd is absolute
		if((new File(pCmd)).isAbsolute()) {
			return pCmd;
		}

		// special processing on windows-systems.
		// File.pathSeparator is hopefully more robust than
		// System.getProperty("os.name") ?!
		boolean isWindows = File.pathSeparator.equals(";");

		String dirs[] = pPath.split(File.pathSeparator);
		for(int i = 0; i < dirs.length; ++i) {
			if(isWindows) {
				// try thre typical extensions
				File cmd = new File(dirs[i], pCmd + ".exe");
				if(cmd.exists()) {
					return cmd.getPath();
				}
				cmd = new File(dirs[i], pCmd + ".cmd");
				if(cmd.exists()) {
					return cmd.getPath();
				}
				cmd = new File(dirs[i], pCmd + ".bat");
				if(cmd.exists()) {
					return cmd.getPath();
				}
			} else {
				File cmd = new File(dirs[i], pCmd);
				if(cmd.exists()) {
					return cmd.getPath();
				}
			}
		}
		throw new FileNotFoundException(pCmd);
	}
}
