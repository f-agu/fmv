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
import java.util.List;
import java.util.Objects;

import org.fagu.fmv.mymedia.sync.Item;
import org.fagu.fmv.mymedia.sync.Storage;
import org.fagu.fmv.mymedia.sync.Synchronizer;


/**
 * @author f.agu
 */
public class WrappedSynchronizer implements Synchronizer {

	protected final Synchronizer synchronizer;

	/**
	 * @param synchronizer
	 * @param printStream
	 */
	public WrappedSynchronizer(Synchronizer synchronizer) {
		this.synchronizer = Objects.requireNonNull(synchronizer);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#start(org.fagu.fmv.mymedia.sync.Storage, java.util.List)
	 */
	@Override
	public void start(Storage sourceStorage, List<Storage> destStorages) {
		synchronizer.start(sourceStorage, destStorages);
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		synchronizer.close();
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#doNothingOnFile(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void doNothingOnFile(Item item) {
		synchronizer.doNothingOnFile(item);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#doNothingOnFolder(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void doNothingOnFolder(Item item) {
		synchronizer.doNothingOnFolder(item);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#mkdir(org.fagu.fmv.mymedia.sync.Item, java.lang.String)
	 */
	@Override
	public Item mkdir(Item destItem, String name) throws IOException {
		return synchronizer.mkdir(destItem, name);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#createFile(org.fagu.fmv.mymedia.sync.Item, java.lang.String)
	 */
	@Override
	public Item createFile(Item destItem, String name) throws IOException {
		return synchronizer.createFile(destItem, name);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#copyForNew(org.fagu.fmv.mymedia.sync.Item,
	 *      org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void copyForNew(Item srcItem, Item destItem) throws IOException {
		synchronizer.copyForNew(srcItem, destItem);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#copyForUpdate(org.fagu.fmv.mymedia.sync.Item,
	 *      org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void copyForUpdate(Item srcItem, Item destItem) throws IOException {
		synchronizer.copyForUpdate(srcItem, destItem);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#delete(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public boolean delete(Item item) throws IOException {
		return synchronizer.delete(item);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#conflict(java.lang.String)
	 */
	@Override
	public void conflict(String path) {
		synchronizer.conflict(path);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#ignore(java.lang.String)
	 */
	@Override
	public void ignore(String path) {
		synchronizer.ignore(path);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#unknown(java.lang.String)
	 */
	@Override
	public void unknown(String path) {
		synchronizer.unknown(path);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Wrapped(" + synchronizer + ')';
	}

	/**
	 * @return
	 */
	public Synchronizer getDelegatedSynchronizer() {
		return synchronizer;
	}

}
