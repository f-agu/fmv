package org.fagu.fmv.mymedia.ebook.checktree;

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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.ebook.EBooksFile;


/**
 * @author Utilisateur
 * @created 28 mai 2018 08:59:51
 */
public class Bootstrap {

	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			System.out.println("Usage: java cp . " + Bootstrap.class.getName() + " <ebook-folder>");
			return;
		}
		File root = new File(args[0]);
		if( ! root.exists()) {
			throw new FileNotFoundException(root.toString());
		}
		checkLevel1(root);
	}

	private static void checkLevel1(File root) {
		File[] letterFolders = root.listFiles();
		for(File letterFolder : letterFolders) {
			if( ! letterFolder.isDirectory()) {
				System.out.println("Failed: " + letterFolder + " is not a folder");
				continue;
			}
			String name = letterFolder.getName();

			if(name.length() != 1 || 'A' > name.charAt(0) || name.charAt(0) > 'Z') {
				System.out.println("Failed: " + letterFolder + " must be have a name with one upper letter");
				continue;
			}
			checkLevel2(letterFolder);
		}
	}

	private static void checkLevel2(File letterFolder) {
		File[] authorFolders = letterFolder.listFiles();
		for(File authorFolder : authorFolders) {
			if( ! authorFolder.isDirectory()) {
				System.out.println("Failed: " + authorFolder + " is not a folder");
				continue;
			}
			checkLevel3(authorFolder);
		}
	}

	private static void checkLevel3(File authorFolder) {
		String author = authorFolder.getName();
		File[] epubFiles = authorFolder.listFiles();
		for(File epubFile : epubFiles) {
			if(epubFile.isDirectory()) {
				System.out.println("Failed: " + epubFile + " is not a file");
				continue;
			}
			String name = epubFile.getName();
			String extension = FilenameUtils.getExtension(name);
			if( ! "epub".equalsIgnoreCase(extension) && ! "pdf".equalsIgnoreCase(extension)) {
				System.out.println("Failed: " + epubFile + " is not a epub or pdf file");
			}
			if("epub".equalsIgnoreCase(extension)) {
				try {
					checkLevel4(epubFile, author);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void checkLevel4(File epubFile, String author) throws IOException {
		EBooksFile eBooksFile = EBooksFile.open(epubFile);
		String title = eBooksFile.getTitle();

		String name = epubFile.getName();
		String wantedName = author + " - " + title + ".epub";
		wantedName = wantedName.replace(':', ',');
		wantedName = wantedName.replace('?', ' ');
		wantedName = wantedName.replace('/', '-');
		wantedName = wantedName.replaceAll("\"", "");

		if(wantedName.equals(name)) {
			return;
		}
		System.out.println(name + "  ===>  " + wantedName);

		// if( ! name.startsWith(author)) {
		//
		// }
	}

}
