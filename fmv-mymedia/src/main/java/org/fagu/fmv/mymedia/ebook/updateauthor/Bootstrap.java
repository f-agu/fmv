package org.fagu.fmv.mymedia.ebook.updateauthor;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.fagu.fmv.mymedia.ebook.EBooksFile;


/**
 * @author fagu
 */
public class Bootstrap {

	private static void updateForFolder(File folder) {
		File[] listFolders = folder.listFiles(f -> f.isDirectory());
		File[] listFiles = folder.listFiles(epubFilter());
		if(ArrayUtils.isEmpty(listFolders)) {
			if(ArrayUtils.isNotEmpty(listFiles)) {
				updateAuthor(folder);
				return;
			}
			System.out.println("Empty folder : " + folder.getAbsolutePath());
		} else {
			if(ArrayUtils.isEmpty(listFiles)) {
				for(File subFolder : listFolders) {
					updateForFolder(subFolder);
				}
			} else {
				System.out.println("Folder contains folders and files ! : " + folder.getAbsolutePath());
			}
		}
	}

	private static void updateAuthor(File folder) {
		File[] files = folder.listFiles(epubFilter());
		if(files == null) {
			return;
		}
		updateAuthor(Arrays.asList(files), folder.getName());
	}

	private static void updateAuthor(Collection<File> files, String author) {
		Map<String, String> metadataMap = new HashMap<>();
		metadataMap.put("creator", author);
		metadataMap.put("publisher", "nobody");
		metadataMap.put("contributor", " ");

		System.out.println();
		System.out.println("Update author with '" + author + '\'');

		for(File file : files) {
			if( ! file.isFile()) {
				return;
			}
			System.out.print("    " + file.getName() + "...");
			try {
				EBooksFile eBooksFile = EBooksFile.open(file);
				if(eBooksFile.needToWriteMetadatas(metadataMap)) {
					System.out.println("   [updating]");
					File newFile = eBooksFile.writeMetadatas(metadataMap);
					file.delete();
					newFile.renameTo(file);
				} else {
					System.out.println();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static FileFilter epubFilter() {
		return f -> f.isFile() && "epub".equalsIgnoreCase(FilenameUtils.getExtension(f.getName()));
	}

	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Usage: java " + Bootstrap.class.getName() + " <folder|file>");
			return;
		}

		File file = new File(args[0]);
		if(epubFilter().accept(file)) {
			updateAuthor(Collections.singletonList(file), file.getParentFile().getName());
		} else if(file.isDirectory()) {
			updateForFolder(file);
		}
	}

}
