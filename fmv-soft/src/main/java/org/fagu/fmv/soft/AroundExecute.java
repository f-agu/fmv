package org.fagu.fmv.soft;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.fagu.fmv.soft.AroundExecutes.SelectFolderTemporaryFolderBuilder;
import org.fagu.fmv.soft.exec.FMVExecutor;


/**
 * @author f.agu
 * @created 13 nov. 2020 16:56:30
 */
public interface AroundExecute extends Closeable {

	void initialize(FMVExecutor fmvExecutor, CommandLine command, Map<String, String> environment) throws IOException;

	static AroundExecute nothing() {
		return AroundExecutes.NOTHING_AROUND_EXECUTE;
	}

	static SelectFolderTemporaryFolderBuilder temporaryFolder() throws IOException {
		return new SelectFolderTemporaryFolderBuilder();
	}

}
