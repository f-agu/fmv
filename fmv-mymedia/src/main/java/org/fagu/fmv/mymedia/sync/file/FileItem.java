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

	public FileItem(File file) {
		this.file = Objects.requireNonNull(file);
	}

	@Override
	public boolean isFile() {
		return file.isFile();
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public long size() {
		return file.length();
	}

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

	@Override
	public Item mkdir(String name) throws IOException {
		File newFile = new File(file, name);
		newFile.mkdirs();
		return new FileItem(newFile);
	}

	@Override
	public Item createFile(String name) throws IOException {
		return new FileItem(new File(file, name));
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return new FileOutputStream(file);
	}

	@Override
	public boolean delete() throws IOException {
		return delete(file);
	}

	@Override
	public String toString() {
		return file.getPath();
	}

	// ********************************************

	private static boolean delete(File file) {
		if(file.isDirectory()) {
			for(File f : file.listFiles()) {
				delete(f);
			}
		}
		return file.delete();
	}

}
