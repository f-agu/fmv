package org.fagu.fmv.soft;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;


/**
 * @author Oodrive
 * @author f.agu
 * @created 13 nov. 2020 16:57:33
 */
class TemporaryFolders {

	static final TemporaryFolder EMPTY = new EmptyTemporaryFolder();

	private TemporaryFolders() {}

	// ----------------------------------------------------

	static class EmptyTemporaryFolder implements TemporaryFolder {

		private EmptyTemporaryFolder() {}

		@Override
		public void close() throws IOException {}

		@Override
		public void initialize(SoftExecutor softExecutor) {}

		@Override
		public File getFile() {
			throw new RuntimeException("Empty temporary folder");
		}

	}

	// ----------------------------------------------------

	static class FileTemporaryFolder implements TemporaryFolder {

		private final File folder;

		private final boolean toDelete;

		FileTemporaryFolder(File folder, boolean toDelete) {
			this.folder = Objects.requireNonNull(folder);
			this.toDelete = toDelete;
		}

		@Override
		public void initialize(SoftExecutor softExecutor) {
			String tmpFolderPath = folder.getAbsolutePath();
			softExecutor
					.addEnv("TMPDIR", tmpFolderPath)
					.addEnv("TEMP", tmpFolderPath)
					.addEnv("TMP", tmpFolderPath);
		}

		@Override
		public File getFile() {
			return folder;
		}

		@Override
		public void close() throws IOException {
			if(toDelete) {
				FileUtils.deleteDirectory(folder);
			}
		}

		@Override
		public String toString() {
			return "FileTemporaryFolder[" + folder.getAbsolutePath() + "]";
		}
	}
}
