package org.fagu.fmv.soft;

import java.io.File;
import java.util.Objects;


/**
 * @author f.agu
 * @created 16 nov. 2020 09:26:39
 */
public class DeleteInfo {

	private final File file;

	private final int countFiles;

	private final int countFolders;

	private final long size;

	private DeleteInfo(File file, int countFiles, int countFolders, long size) {
		if(countFiles < 0) {
			throw new IllegalArgumentException("countFiles must be positive: " + countFiles);
		}
		if(countFolders < 0) {
			throw new IllegalArgumentException("countFolders must be positive: " + countFolders);
		}
		if(size < 0) {
			throw new IllegalArgumentException("size must be positive: " + size);
		}
		this.file = Objects.requireNonNull(file);
		this.countFiles = countFiles;
		this.countFolders = countFolders;
		this.size = size;
	}

	public static DeleteInfo empty(File file) {
		return with(file, 0, 0, 0);
	}

	public static DeleteInfo with(File file, int countFiles, int countFolders, long size) {
		return new DeleteInfo(file, countFiles, countFolders, size);
	}

	public File getFile() {
		return file;
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
		return with(file, countFiles + addCountFiles, countFolders + addCountFolders, size + addSize);
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
