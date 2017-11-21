package org.fagu.fmv.soft;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author Oodrive
 * @author f.agu
 * @created 21 nov. 2017 12:06:15
 */
@FunctionalInterface
public interface ExecuteDelegate {

	/**
	 * @param fmvExecutor
	 * @param commandLine
	 * @return
	 * @throws IOException
	 */
	int execute(FMVExecutor fmvExecutor, CommandLine commandLine) throws IOException;

}
