package org.fagu.fmv.mymedia.logger;

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
