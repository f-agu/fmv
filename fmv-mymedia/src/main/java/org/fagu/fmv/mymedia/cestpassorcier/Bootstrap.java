package org.fagu.fmv.mymedia.cestpassorcier;

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
import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.file.FileUtils;


/**
 * @author fagu
 */
public class Bootstrap {

	private static File findFolder() {
		return FileUtils.findFirstHarddriveFaguVv()
				.map(Bootstrap::findFolder)
				.filter(Objects::nonNull)
				.orElseThrow(() -> new RuntimeException("Disk not found: fagu_Vv_1 or fagu_Vv_2"));
	}

	private static File findFolder(File ddFile) {
		File file = new File(ddFile, "Documentaires");
		file = new File(file, "_C'est pas sorcier");
		return file.exists() ? file : null;
	}

	private static String getTitle(File file) {
		String name = file.getName();
		String lcName = name.toLowerCase();
		for(String search : Arrays.asList("c'est pas sorcier - ", "quelle aventure - ")) {
			int idx = lcName.indexOf(search);
			if(idx == 0) {
				return FilenameUtils.getBaseName(name.substring(search.length()));
			}
		}
		System.out.println("NOT FOUND: " + file.getAbsolutePath());
		return name;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File folder = findFolder();

		for(File cat1File : folder.listFiles(File::isDirectory)) {
			for(File cat2File : cat1File.listFiles()) {
				if(cat2File.isFile()) {
					System.out.println(cat1File.getName() + " / " + getTitle(cat2File));
				} else if(cat2File.isDirectory()) {
					for(File file : cat2File.listFiles(File::isFile)) {
						System.out.println(cat1File.getName() + " / " + cat2File.getName() + " / " + getTitle(file));
					}
				}
			}
		}
	}

}
