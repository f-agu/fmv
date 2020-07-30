package org.fagu.fmv.soft;

import java.util.Objects;


/**
 * @author Oodrive
 * @author f.agu
 * @created 30 juil. 2020 12:10:38
 */
public class ExecuteDelegateRepository {

	private static ExecuteDelegate delegate = BasicExecuteDelegate.INSTANCE;

	private ExecuteDelegateRepository() {}

	public static ExecuteDelegate get() {
		return delegate;
	}

	public static void set(ExecuteDelegate executeDelegate) {
		delegate = Objects.requireNonNull(executeDelegate);
	}
}
