package org.fagu.fmv.soft;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.soft.exception.NoEnoughSpaceOnDeviceException;


/**
 * @author Oodrive
 * @author f.agu
 * @created 13 nov. 2020 16:57:33
 */
class AroundExecutes {

	static final AroundExecute NOTHING_AROUND_EXECUTE = new NothingAroundExecute();

	static final FolderChecker NOTHING_FOLDER_CHECKER = f -> {};

	private AroundExecutes() {}

	// ----------------------------------------------------

	static class NothingAroundExecute implements AroundExecute {

		private NothingAroundExecute() {}

		@Override
		public void initialize(SoftExecutor softExecutor) {}

		@Override
		public void close() throws IOException {}

		@Override
		public String toString() {
			return "NothingAroundExecute";
		}

	}

	// ----------------------------------------------------

	public static class SelectFolderTemporaryFolderBuilder {

		SelectFolderTemporaryFolderBuilder() {}

		public TemporaryFolderBuilder use(File tmpFolder) throws IOException {
			FileUtils.forceMkdir(tmpFolder);
			return new TemporaryFolderBuilder(tmpFolder, false);
		}

		public TemporaryFolderBuilder createSubFolder(File rootTmpFolder) throws IOException {
			return createSubFolder(rootTmpFolder, "fmv-");
		}

		public TemporaryFolderBuilder createSubFolder(File rootTmpFolder, String prefix) throws IOException {
			FileUtils.forceMkdir(rootTmpFolder);
			File folder = org.fagu.fmv.utils.file.FileUtils.getTempFolder(prefix, "", rootTmpFolder);
			return new TemporaryFolderBuilder(folder, true);
		}

	}

	// ----------------------------------------------------

	public static class TemporaryFolderBuilder {

		private final File tmpFolder;

		private boolean deleleteAtClose;

		private Set<String> envNames = new HashSet<>(Arrays.asList("TMPDIR", "TEMP", "TMP"));

		private FolderChecker folderChecker;

		private Consumer<DeleteInfo> deleteInfoConsumer;

		private TemporaryFolderBuilder(File tmpFolder, boolean deleleteAtClose) {
			this.tmpFolder = Objects.requireNonNull(tmpFolder);
		}

		public TemporaryFolderBuilder setEnvironmentNames(Collection<String> envNames) {
			if(envNames != null && ! envNames.isEmpty()) {
				this.envNames = new HashSet<>(envNames);
			}
			return this;
		}

		public TemporaryFolderBuilder addEnvironmentNames(String... envNames) {
			return addEnvironmentNames(Arrays.asList(envNames));
		}

		public TemporaryFolderBuilder addEnvironmentNames(Collection<String> envNames) {
			if(envNames != null && ! envNames.isEmpty()) {
				this.envNames.addAll(envNames);
			}
			return this;
		}

		public TemporaryFolderBuilder withMinimumFreeSpace(long minSpace) {
			if(minSpace > 0) {
				this.folderChecker = folder -> {
					if(folder.getFreeSpace() < minSpace) {
						throw new NoEnoughSpaceOnDeviceException("FreeSpace: " + folder.getFreeSpace() + " ; need " + minSpace);
					}
				};
			}
			return this;
		}

		public TemporaryFolderBuilder withFolderChecker(FolderChecker folderChecker) {
			this.folderChecker = folderChecker;
			return this;
		}

		public TemporaryFolderBuilder withDeleteInfoConsumer(Consumer<DeleteInfo> deleteInfoConsumer) {
			this.deleteInfoConsumer = deleteInfoConsumer;
			return this;
		}

		public TemporaryFolderAroundExecute build() {
			return new TemporaryFolderAroundExecute(this);
		}

	}

	// ----------------------------------------------------

	public static interface FolderChecker {

		void check(File folder) throws IOException;

	}

	// ----------------------------------------------------

	public static class DeleteInfo {

		private static final DeleteInfo EMPTY = new DeleteInfo(0, 0, 0);

		private final int countFiles;

		private final int countFolders;

		private final long size;

		private DeleteInfo(int countFiles, int countFolders, long size) {
			if(countFiles < 0) {
				throw new IllegalArgumentException("countFiles must be positive: " + countFiles);
			}
			if(countFolders < 0) {
				throw new IllegalArgumentException("countFolders must be positive: " + countFolders);
			}
			if(size < 0) {
				throw new IllegalArgumentException("size must be positive: " + size);
			}
			this.countFiles = countFiles;
			this.countFolders = countFolders;
			this.size = size;
		}

		public static DeleteInfo empty() {
			return EMPTY;
		}

		public static DeleteInfo with(int countFiles, int countFolders, long size) {
			if(countFiles == 0 && countFolders == 0 && size == 0) {
				return EMPTY;
			}
			return new DeleteInfo(countFiles, countFolders, size);
		}

		public int getCountFiles() {
			return countFiles;
		}

		public int getCountFolders() {
			return countFolders;
		}

		public long getSize() {
			return size;
		}

		public DeleteInfo add(int addCountFiles, int addCountFolders, long addSize) {
			return with(countFiles + addCountFiles, countFolders + addCountFolders, size + addSize);
		}

		public DeleteInfo addFile(long addSize) {
			return add(1, 0, addSize);
		}

		public DeleteInfo addFiles(int countFiles, long addSize) {
			return add(countFiles, 0, addSize);
		}

		public DeleteInfo addFolder() {
			return add(0, 1, 0);
		}

		public DeleteInfo add(DeleteInfo deleteInfo) {
			return add(deleteInfo.countFiles, deleteInfo.countFolders, deleteInfo.size);
		}
	}

	// ----------------------------------------------------

	static class TemporaryFolderAroundExecute implements AroundExecute {

		private final File folder;

		private final boolean deleleteAtClose;

		private final Set<String> envNames;

		private final FolderChecker folderChecker;

		private final Consumer<DeleteInfo> deleteInfoConsumer;

		TemporaryFolderAroundExecute(TemporaryFolderBuilder builder) {
			this.folder = builder.tmpFolder;
			this.deleleteAtClose = builder.deleleteAtClose;
			this.envNames = Collections.unmodifiableSet(new HashSet<>(builder.envNames));
			this.folderChecker = builder.folderChecker != null ? builder.folderChecker : NOTHING_FOLDER_CHECKER;
			this.deleteInfoConsumer = builder.deleteInfoConsumer != null ? builder.deleteInfoConsumer : di -> {};
		}

		@Override
		public void initialize(SoftExecutor softExecutor) throws IOException {
			folderChecker.check(folder);
			String tmpFolderPath = folder.getAbsolutePath();
			envNames.forEach(env -> softExecutor.addEnv(env, tmpFolderPath));
		}

		@Override
		public void close() throws IOException {
			if(deleleteAtClose) {
				DeleteInfo info = delete(folder);
				deleteInfoConsumer.accept(info);
			}
		}

		@Override
		public String toString() {
			return "TemporaryFolderAroundExecute[" + folder.getAbsolutePath() + "]";
		}
	}

	// **********************************************************************

	private static DeleteInfo delete(File file) {
		if( ! file.exists()) {
			return DeleteInfo.empty();
		}

		DeleteInfo deleteInfo = DeleteInfo.empty();

		if(file.isDirectory()) {
			File[] children = file.listFiles();
			if(children != null) {
				for(File child : children) {
					deleteInfo = deleteInfo.add(delete(child));
				}
			}

			if(file.delete()) {
				deleteInfo = deleteInfo.addFolder();
			}
		} else if(file.isFile()) {
			long size = file.length();
			if(file.delete()) {
				deleteInfo = deleteInfo.addFile(size);
			}
		}
		return deleteInfo;
	}
}
