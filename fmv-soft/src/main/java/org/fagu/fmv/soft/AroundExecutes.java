package org.fagu.fmv.soft;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.exec.exception.NoEnoughSpaceOnDeviceException;


/**
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
		public void initialize(FMVExecutor fmvExecutor, CommandLine command, Map<String, String> environment) {}

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

		private boolean deleteAtClose;

		private Set<String> envNames = new HashSet<>(Arrays.asList("TMPDIR", "TEMP", "TMP"));

		private FolderChecker folderChecker;

		private Consumer<DeleteInfo> deleteInfoConsumer;

		private TemporaryFolderBuilder(File tmpFolder, boolean deleteAtClose) {
			this.tmpFolder = Objects.requireNonNull(tmpFolder);
			this.deleteAtClose = deleteAtClose;
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

		public TemporaryFolderBuilder withDeleteAtClose(boolean deleteAtClose) {
			this.deleteAtClose = deleteAtClose;
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

	static class TemporaryFolderAroundExecute implements AroundExecute {

		private final File folder;

		private final boolean deleteAtClose;

		private final Set<String> envNames;

		private final FolderChecker folderChecker;

		private final Consumer<DeleteInfo> deleteInfoConsumer;

		TemporaryFolderAroundExecute(TemporaryFolderBuilder builder) {
			this.folder = builder.tmpFolder;
			this.deleteAtClose = builder.deleteAtClose;
			this.envNames = Collections.unmodifiableSet(new HashSet<>(builder.envNames));
			this.folderChecker = builder.folderChecker != null ? builder.folderChecker : NOTHING_FOLDER_CHECKER;
			this.deleteInfoConsumer = builder.deleteInfoConsumer != null ? builder.deleteInfoConsumer : di -> {};
		}

		@Override
		public void initialize(FMVExecutor fmvExecutor, CommandLine command, Map<String, String> environment) throws IOException {
			folderChecker.check(folder);
			String tmpFolderPath = folder.getAbsolutePath();
			envNames.forEach(env -> environment.put(env, tmpFolderPath));
		}

		@Override
		public void close() throws IOException {
			if(deleteAtClose) {
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
			return DeleteInfo.empty(file);
		}

		DeleteInfo deleteInfo = DeleteInfo.empty(file);

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
