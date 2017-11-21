package org.fagu.fmv.soft;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author Oodrive
 * @author f.agu
 * @created 21 nov. 2017 12:07:21
 */
public class EnvironmentExecuteDelegate implements ExecuteDelegate {

	private final Map<String, String> envs = new HashMap<>();

	/**
	 * 
	 */
	public EnvironmentExecuteDelegate() {}

	/**
	 * @param envs
	 */
	public EnvironmentExecuteDelegate(Map<String, String> envs) {
		this.envs.putAll(envs);
	}

	/**
	 * @see org.fagu.fmv.soft.ExecuteDelegate#execute(org.fagu.fmv.soft.exec.FMVExecutor,
	 *      org.apache.commons.exec.CommandLine)
	 */
	@Override
	public int execute(FMVExecutor fmvExecutor, CommandLine commandLine) throws IOException {
		return fmvExecutor.execute(commandLine);
	}

}
