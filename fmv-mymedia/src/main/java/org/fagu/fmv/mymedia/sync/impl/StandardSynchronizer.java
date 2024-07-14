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
import java.util.concurrent.atomic.AtomicLong;

import org.fagu.fmv.mymedia.sync.Item;
import org.fagu.fmv.mymedia.sync.Storage;
import org.fagu.fmv.mymedia.sync.Synchronizer;
import org.fagu.fmv.mymedia.utils.IOUtils;


/**
 * @author f.agu
 */
public class StandardSynchronizer implements Synchronizer {

	public StandardSynchronizer() {}

	@Override
	public void doNothingOnFile(Item item) {}

	@Override
	public void doNothingOnFolder(Item item) {}

	@Override
	public void start(Storage sourceStorage, List<Storage> destStorages) {}

	@Override
	public void close() throws IOException {}

	@Override
	public Item mkdir(Item destItem, String name) throws IOException {
		return destItem.mkdir(name);
	}

	@Override
	public Item createFile(Item destItem, String name) throws IOException {
		return destItem.createFile(name);
	}

	@Override
	public void copyForNew(Item srcItem, Item destItem, AtomicLong progress) throws IOException {
		try (InputStream inputStream = srcItem.openInputStream();
				OutputStream outputStream = destItem.openOutputStream()) {
			IOUtils.copy(inputStream, outputStream, progress);
		}
	}

	@Override
	public void copyForUpdate(Item srcItem, Item destItem, AtomicLong progress) throws IOException {
		copyForNew(srcItem, destItem, progress);
	}

	@Override
	public boolean delete(Item item) throws IOException {
		return item.delete();
	}

	@Override
	public void conflict(String path) {}

	@Override
	public void ignore(String path) {}

	@Override
	public void unknown(String path) {}

	@Override
	public String toString() {
		return "Standard";
	}

}
