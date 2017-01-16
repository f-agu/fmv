package org.fagu.fmv.mymedia.sync.file;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 fagu
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.fagu.fmv.mymedia.sync.Item;


/**
 * @author f.agu
 */
public class FileItem implements Item {

	private final File file;

	/**
	 * @param file
	 */
	public FileItem(File file) {
		this.file = Objects.requireNonNull(file);
	}

	/**
	 * @see org.fagu.sync.Item#isFile()
	 */
	@Override
	public boolean isFile() {
		return file.isFile();
	}

	/**
	 * @see org.fagu.sync.Item#isDirectory()
	 */
	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	/**
	 * @see org.fagu.sync.Item#getName()
	 */
	@Override
	public String getName() {
		return file.getName();
	}

	/**
	 * @see org.fagu.sync.Item#size()
	 */
	@Override
	public long size() {
		return file.length();
	}

	/**
	 * @see org.fagu.sync.Item#listChildren()
	 */
	@Override
	public Map<String, Item> listChildren() throws IOException {
		File[] listFiles = file.listFiles();
		if(listFiles == null || listFiles.length == 0) {
			return Collections.emptyMap();
		}
		Map<String, Item> items = new TreeMap<>(Collections.reverseOrder());
		for(File file : listFiles) {
			items.put(file.getName(), new FileItem(file));
		}
		return items;
	}

	/**
	 * @see org.fagu.sync.Item#mkdir(java.lang.String)
	 */
	@Override
	public Item mkdir(String name) throws IOException {
		File newFile = new File(file, name);
		newFile.mkdirs();
		return new FileItem(newFile);
	}

	/**
	 * @see org.fagu.sync.Item#createFile(java.lang.String)
	 */
	@Override
	public Item createFile(String name) throws IOException {
		return new FileItem(new File(file, name));
	}

	/**
	 * @see org.fagu.sync.Item#openInputStream()
	 */
	@Override
	public InputStream openInputStream() throws IOException {
		return new FileInputStream(file);
	}

	/**
	 * @see org.fagu.sync.Item#openOutputStream()
	 */
	@Override
	public OutputStream openOutputStream() throws IOException {
		return new FileOutputStream(file);
	}

	/**
	 * @see org.fagu.sync.Item#delete()
	 */
	@Override
	public boolean delete() throws IOException {
		return delete(file);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return file.getPath();
	}

	// ********************************************

	/**
	 * @param file
	 * @return
	 */
	private static boolean delete(File file) {
		if(file.isDirectory()) {
			for(File f : file.listFiles()) {
				delete(f);
			}
		}
		return file.delete();
	}

}
