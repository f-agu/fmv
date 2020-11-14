package org.fagu.fmv.soft;

import java.io.Closeable;
import java.io.IOException;

import org.fagu.fmv.soft.AroundExecutes.SelectFolderTemporaryFolderBuilder;


/**
 * @author Oodrive
 * @author f.agu
 * @created 13 nov. 2020 16:56:30
 */
public interface AroundExecute extends Closeable {

	void initialize(SoftExecutor softExecutor) throws IOException;

	static AroundExecute nothing() {
		return AroundExecutes.NOTHING_AROUND_EXECUTE;
	}

	static SelectFolderTemporaryFolderBuilder temporaryFolder() throws IOException {
		return new SelectFolderTemporaryFolderBuilder();
	}

}
