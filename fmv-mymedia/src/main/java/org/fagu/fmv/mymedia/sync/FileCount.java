package org.fagu.fmv.mymedia.sync;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;


/**
 * @author f.agu
 */
public class FileCount {

	private int countFiles;

	private int countFolders;

	private long fileSize;

	/**
	 * @return the countFiles
	 */
	public int getCountFiles() {
		return countFiles;
	}

	/**
	 * @return the countFolders
	 */
	public int getCountFolders() {
		return countFolders;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param file
	 */
	public void addFile(File file) {
		addFile(file.length());
	}

	/**
	 * @param size
	 */
	public void addFile(long size) {
		++countFiles;
		fileSize += size;
	}

	/**
	 * 
	 */
	public void addFolder() {
		++countFolders;
	}

}
