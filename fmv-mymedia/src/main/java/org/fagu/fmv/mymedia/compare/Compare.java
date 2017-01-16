package org.fagu.fmv.mymedia.compare;

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


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;


/**
 * @author f.agu
 */
public class Compare {

	/**
	 * 
	 */
	public Compare() {}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file1 = new File("C:\\tmp\\auralux\\orig\\Auralux_1.85");
		File file2 = new File("C:\\tmp\\auralux\\dwl1\\Auralux-v1-85-Pdalife.ru");
		compare(file1, file2);
	}

	/**
	 * @param file1
	 * @param file2
	 * @throws IOException
	 */
	private static void compare(File file1, File file2) throws IOException {
		if(file1.isDirectory() && file2.isDirectory()) {
			compareFolders(file1, file2);
		} else if(file1.isFile() && file2.isFile()) {
			compareFiles(file1, file2);
		} else {
			System.out.println("Unable to compare " + file1 + " and " + file2);
		}
	}

	/**
	 * @param folder1
	 * @param folder2
	 * @throws IOException
	 */
	private static void compareFolders(File folder1, File folder2) throws IOException {
		File[] childs = folder1.listFiles();
		Set<String> existSet = new HashSet<>();
		if(childs != null) {
			for(File child : childs) {
				existSet.add(child.getName());
				File other = new File(folder2, child.getName());
				if( ! other.exists()) {
					System.out.println("Missing " + other);
					continue;
				}
				compare(child, other);
			}
		}

		childs = folder2.listFiles();
		if(childs != null) {
			for(File child : childs) {
				existSet.remove(child.getName());
			}
		}
		if( ! existSet.isEmpty()) {
			for(String name : existSet) {
				System.out.println("Missing " + new File(folder1, name));
			}
		}
	}

	/**
	 * @param file1
	 * @param file2
	 * @throws IOException
	 */
	private static void compareFiles(File file1, File file2) throws IOException {
		if(file1.length() != file2.length()) {
			System.out.println("File size " + file1 + " : " + file1.length() + " != " + file2.length());
		}
		int count = 0;
		try (InputStream inputStream1 = new BufferedInputStream(new FileInputStream(file1));
				InputStream inputStream2 = new BufferedInputStream(new FileInputStream(file2))) {
			int read;
			Integer start = null;
			int pos = 0;
			while((read = inputStream1.read()) >= 0) {
				++pos;
				if(read != inputStream2.read()) {
					start = pos;
				} else if(start != null) {
					System.out.println("Diff " + file1 + " between " + start + " and " + pos);
					start = null;
					++count;
					if(count > 5) {
						break;
					}
				}
			}
		}
	}
}
