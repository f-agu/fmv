package org.fagu.fmv.mymedia.movie;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2016 fagu
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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;


/**
 * @author f.agu
 */
public class SyncMinSize {

	/**
	 *
	 */
	public SyncMinSize() {}

	/**
	 * @param srcFile
	 * @param destFile
	 */
	public void sync(File srcFile, File destFile) throws IOException {
		File[] srcFiles = srcFile.listFiles();
		if(srcFiles != null) {
			for(File srcF : srcFiles) {
				File findFile = findInDestination(srcF, destFile);
				if(srcF.isDirectory()) {
					if( ! findFile.exists()) {
						findFile.mkdirs();
					}
					sync(srcF, findFile);
				} else {
					if(findFile == null) {
						replaceFile(srcF, destFile, findFile);
					} else {
						long srcSize = srcF.length();
						long destSize = findFile.length();
						if(srcSize < destSize) {
							replaceFile(srcF, destFile, findFile);
						} else {
							// DO NOTHING
						}
					}
				}
			}
		}
	}

	// ****************************************

	/**
	 * @param srcFile
	 * @param destFolder
	 * @return
	 */
	private File findInDestination(File srcFile, File destFolder) throws IOException {
		String name = srcFile.getName();
		if(srcFile.isDirectory()) {
			return new File(destFolder, name);
		}
		String baseName = FilenameUtils.getBaseName(name);
		File[] listFiles = destFolder.listFiles(pathname -> baseName.equals(FilenameUtils.getBaseName(pathname.getName())));
		if(ArrayUtils.isEmpty(listFiles)) {
			return null;
		}
		if(listFiles.length == 1) {
			return listFiles[0];
		}
		throw new IOException("Too many files find '" + baseName + "' in " + destFolder);
	}

	/**
	 * @param srcFile
	 * @param destFile
	 */
	private void replaceFile(File srcFile, File destFolder, File destFile) throws IOException {
		System.out.println("Replace " + srcFile + " -> " + destFolder);
		if(destFile != null) {
			destFile.delete();
		}
		File outFile = new File(destFolder, srcFile.getName());
		FileUtils.copyFile(srcFile, outFile);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SyncMinSize syncMinSize = new SyncMinSize();
		syncMinSize.sync(new File("D:\\Personnel\\TODO\\C'est pas sorcier"), new File("F:\\DD\\C'est pas sorcier"));
	}
}
