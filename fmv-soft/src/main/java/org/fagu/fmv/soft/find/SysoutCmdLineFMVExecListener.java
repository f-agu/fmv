package org.fagu.fmv.soft.find;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteResultHandler;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.FMVExecListener;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author Oodrive
 * @author f.agu
 * @created 20 mars 2025 09:59:09
 */
public class SysoutCmdLineFMVExecListener implements FMVExecListener {

	@Override
	public void eventPreExecute(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
			ExecuteResultHandler handler) {
		System.out.println("Executing " + CommandLineUtils.toLine(command) + " , env: " + environment);
	}

	@Override
	public void eventPostExecute(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
			ExecuteResultHandler handler) {
		System.out.println("Executed " + CommandLineUtils.toLine(command) + " , env: " + environment);
	}

	@Override
	public void eventFailed(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment, ExecuteResultHandler handler,
			IOException ioe) {
		System.out.println("Execute failed " + CommandLineUtils.toLine(command) + " , env: " + environment);
		ioe.printStackTrace();
	}

}
