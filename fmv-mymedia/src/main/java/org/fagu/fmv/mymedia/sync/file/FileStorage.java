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
import java.io.IOException;

import org.fagu.fmv.mymedia.sync.Item;
import org.fagu.fmv.mymedia.sync.Storage;


/**
 * @author f.agu
 */
public class FileStorage implements Storage {

	private final Item root;

	/**
	 * @param files
	 */
	public FileStorage(File file) {
		root = new FileItem(file);
	}

	/**
	 * @see org.fagu.sync.Storage#getRoot()
	 */
	@Override
	public Item getRoot() throws IOException {
		return root;
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "file://" + root;
	}

}
