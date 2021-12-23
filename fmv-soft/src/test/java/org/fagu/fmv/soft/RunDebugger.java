package org.fagu.fmv.soft;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteResultHandler;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.FMVCommandLine;
import org.fagu.fmv.soft.exec.FMVExecListener;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.io.StreamLog;


/**
 * @author Oodrive
 * @author f.agu
 * @created 23 déc. 2021 13:27:28
 */
public class RunDebugger {

	public static void main(String[] args) throws Exception {
		File execFile = new File("C:\\Program Files\\gnuplot\\bin\\gnuplot.exe");

		StreamLog.debug(true);
		FMVExecutor executor = FMVExecutor.with(execFile.getParentFile())
				.err(line -> System.out.println("ReadLine.err: " + line))
				.out(line -> System.out.println("ReadLine.out: " + line))
				.build();
		executor.addListener(new FMVExecListener() {

			@SuppressWarnings({"unchecked", "rawtypes"})
			@Override
			public void eventPreExecute(FMVExecutor fmvExecutor, CommandLine command, Map environment, ExecuteResultHandler handler) {
				System.out.println();
				System.out.println("CommandLine:");
				System.out.println("  " + CommandLineUtils.toLine(command));
				System.out.println();
				System.out.println("Environment:");
				new TreeMap(environment).forEach((k, v) -> System.out.println("  " + k + " : " + v));
				System.out.println();
			}
		});
		int exitValue = executor.execute(FMVCommandLine.create(execFile, "--version"));

		System.out.println();
		System.out.println("ExitValue: " + exitValue);

	}

}
