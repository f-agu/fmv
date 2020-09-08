package org.fagu.fmv.mymedia.logger;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


/**
 * @author f.agu
 * @created 16 juin 2017 13:21:31
 */
public class LoggerFactory {

	/**
	 * 
	 */
	private LoggerFactory() {}

	/**
	 * @param sourceFile
	 * @param propertyLogFile
	 * @param propertyLogFileDefaultName
	 * @return
	 */
	public static File getLogFile(File sourceFile, String propertyLogFile, String propertyLogFileDefaultName) {
		File sourceFolder = sourceFile;
		if(sourceFolder.isFile()) {
			sourceFolder = sourceFolder.getParentFile();
		}
		String property = System.getProperty(propertyLogFile);
		File logFile = null;
		if(property != null) {
			logFile = new File(property);
			if( ! logFile.isAbsolute()) {
				logFile = new File(sourceFolder, property);
			}
		} else {
			logFile = new File(sourceFolder, propertyLogFileDefaultName);
		}
		logFile.getParentFile().mkdirs();
		return logFile;
	}

	/**
	 * @param logFile
	 * @return
	 * @throws IOException
	 */
	public static Logger openLogger(File logFile) throws IOException {
		final PrintStream printStream = new PrintStream(new FileOutputStream(logFile, true));
		return Loggers.timestamp(Loggers.printStream(printStream, logFile.toString()));
	}

}
