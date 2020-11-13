package org.fagu.fmv.soft;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.soft.TemporaryFolders.FileTemporaryFolder;


/**
 * @author Oodrive
 * @author f.agu
 * @created 13 nov. 2020 16:56:30
 */
public interface TemporaryFolder extends Closeable {

	void initialize(SoftExecutor softExecutor);

	File getFile();

	static TemporaryFolder noFolder() {
		return TemporaryFolders.EMPTY;
	}

	static TemporaryFolder use(File tmpFolder) throws IOException {
		FileUtils.forceMkdir(tmpFolder);
		return new FileTemporaryFolder(tmpFolder, false);
	}

	static TemporaryFolder createSub(File rootTmpFolder) throws IOException {
		return createSub(rootTmpFolder, "fmv-");
	}

	static TemporaryFolder createSub(File rootTmpFolder, String prefix) throws IOException {
		FileUtils.forceMkdir(rootTmpFolder);
		File folder = org.fagu.fmv.utils.file.FileUtils.getTempFolder(prefix, "", rootTmpFolder);
		return new FileTemporaryFolder(folder, true);
	}

}
