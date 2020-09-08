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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.ebook.EBooksFile;


/**
 * @author fagu
 */
public class Bootstrap {

	private static void updateForfolder(File folder) {

	}

	private static void updateAuthor(File folder) {
		File[] files = folder.listFiles(f -> f.isFile() && "epub".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
		if(files == null) {
			return;
		}

		Map<String, String> metadataMap = new HashMap<>();
		metadataMap.put("creator", folder.getName());
		metadataMap.put("publisher", "nobody");
		metadataMap.put("contributor", "");

		System.out.println("Update author with " + folder.getName());
		System.out.println();

		for(File file : files) {
			System.out.println(file.getName() + "...");
			try {
				EBooksFile eBooksFile = EBooksFile.open(file);
				File newFile = eBooksFile.writeMetadatas(metadataMap);
				file.delete();
				newFile.renameTo(file);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Usage: java " + Bootstrap.class.getName() + " <folder>");
			return;
		}

		File folder = new File(args[0]);

	}

}
