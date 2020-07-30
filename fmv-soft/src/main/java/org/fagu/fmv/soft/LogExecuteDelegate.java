package org.fagu.fmv.soft;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author Oodrive
 * @author f.agu
 * @created 30 juil. 2020 14:46:56
 */
public class LogExecuteDelegate implements ExecuteDelegate {

	private final ExecuteDelegate delegated;

	private final Consumer<String> logConsumer;

	public LogExecuteDelegate(Consumer<String> logConsumer) {
		this(BasicExecuteDelegate.INSTANCE, logConsumer);
	}

	public LogExecuteDelegate(ExecuteDelegate delegated, Consumer<String> logConsumer) {
		this.delegated = Objects.requireNonNull(delegated);
		this.logConsumer = logConsumer;
	}

	@Override
	public int execute(FMVExecutor fmvExecutor, CommandLine commandLine) throws IOException {
		Integer exitValue = null;
		try {
			exitValue = delegated.execute(fmvExecutor, commandLine);
		} finally {
			if(logConsumer != null) {
				logConsumer.accept(CommandLineUtils.toLine(commandLine) + " ; exitValue: " + (exitValue != null ? exitValue : "failed"));
			}
		}
		return Objects.requireNonNull(exitValue);
	}

}
