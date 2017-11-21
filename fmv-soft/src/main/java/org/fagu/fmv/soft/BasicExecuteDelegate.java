package org.fagu.fmv.soft;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author Oodrive
 * @author f.agu
 * @created 21 nov. 2017 12:07:21
 */
public class BasicExecuteDelegate implements ExecuteDelegate {

	public static final BasicExecuteDelegate INSTANCE = new BasicExecuteDelegate();

	@Override
	public int execute(FMVExecutor fmvExecutor, CommandLine commandLine) throws IOException {
		return fmvExecutor.execute(commandLine);
	}

}
