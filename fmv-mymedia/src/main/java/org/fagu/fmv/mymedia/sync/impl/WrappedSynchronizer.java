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
import java.util.concurrent.atomic.AtomicLong;

import org.fagu.fmv.mymedia.sync.Item;
import org.fagu.fmv.mymedia.sync.Storage;
import org.fagu.fmv.mymedia.sync.Synchronizer;


/**
 * @author f.agu
 */
public class WrappedSynchronizer implements Synchronizer {

	protected final Synchronizer synchronizer;

	public WrappedSynchronizer(Synchronizer synchronizer) {
		this.synchronizer = Objects.requireNonNull(synchronizer);
	}

	@Override
	public void start(Storage sourceStorage, List<Storage> destStorages) {
		synchronizer.start(sourceStorage, destStorages);
	}

	@Override
	public void close() throws IOException {
		synchronizer.close();
	}

	@Override
	public void doNothingOnFile(Item item) {
		synchronizer.doNothingOnFile(item);
	}

	@Override
	public void doNothingOnFolder(Item item) {
		synchronizer.doNothingOnFolder(item);
	}

	@Override
	public Item mkdir(Item destItem, String name) throws IOException {
		return synchronizer.mkdir(destItem, name);
	}

	@Override
	public Item createFile(Item destItem, String name) throws IOException {
		return synchronizer.createFile(destItem, name);
	}

	@Override
	public void copyForNew(Item srcItem, Item destItem, AtomicLong progress) throws IOException {
		synchronizer.copyForNew(srcItem, destItem, progress);
	}

	@Override
	public void copyForUpdate(Item srcItem, Item destItem, AtomicLong progress) throws IOException {
		synchronizer.copyForUpdate(srcItem, destItem, progress);
	}

	@Override
	public boolean delete(Item item) throws IOException {
		return synchronizer.delete(item);
	}

	@Override
	public void conflict(String path) {
		synchronizer.conflict(path);
	}

	@Override
	public void ignore(String path) {
		synchronizer.ignore(path);
	}

	@Override
	public void unknown(String path) {
		synchronizer.unknown(path);
	}

	@Override
	public String toString() {
		return "Wrapped(" + synchronizer + ')';
	}

	public Synchronizer getDelegatedSynchronizer() {
		return synchronizer;
	}

}
