package org.fagu.fmv.mymedia.sync.impl;

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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.fagu.fmv.mymedia.sync.Item;
import org.fagu.fmv.mymedia.sync.Storage;
import org.fagu.fmv.mymedia.sync.Synchronizer;


/**
 * @author f.agu
 */
public class StandardSynchronizer implements Synchronizer {

	/**
	 * 
	 */
	public StandardSynchronizer() {}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#doNothingOnFile(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void doNothingOnFile(Item item) {}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#doNothingOnFolder(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void doNothingOnFolder(Item item) {}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#start(org.fagu.fmv.mymedia.sync.Storage, java.util.List)
	 */
	@Override
	public void start(Storage sourceStorage, List<Storage> destStorages) {}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#close()
	 */
	@Override
	public void close() {}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#mkdir(org.fagu.fmv.mymedia.sync.Item, java.lang.String)
	 */
	@Override
	public Item mkdir(Item destItem, String name) throws IOException {
		return destItem.mkdir(name);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#createFile(org.fagu.fmv.mymedia.sync.Item, java.lang.String)
	 */
	@Override
	public Item createFile(Item destItem, String name) throws IOException {
		return destItem.createFile(name);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#copyForNew(org.fagu.fmv.mymedia.sync.Item,
	 *      org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void copyForNew(Item srcItem, Item destItem) throws IOException {
		try (InputStream inputStream = srcItem.openInputStream(); OutputStream outputStream = destItem.openOutputStream()) {
			IOUtils.copyLarge(inputStream, outputStream);
		}
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#copyForUpdate(org.fagu.fmv.mymedia.sync.Item,
	 *      org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void copyForUpdate(Item srcItem, Item destItem) throws IOException {
		copyForNew(srcItem, destItem);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#delete(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public boolean delete(Item item) throws IOException {
		return item.delete();
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#conflict(java.lang.String)
	 */
	@Override
	public void conflict(String path) {}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#ignore(java.lang.String)
	 */
	@Override
	public void ignore(String path) {}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#unknown(java.lang.String)
	 */
	@Override
	public void unknown(String path) {}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Standard";
	}

}
