package org.fagu.fmv.mymedia.sync;

import java.io.Closeable;

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
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author f.agu
 */
public interface Synchronizer extends Closeable {

	/**
	 * @param sourceStorage
	 * @param destStorages
	 */
	default void start(Storage sourceStorage, List<Storage> destStorages) {}

	/**
	 * @param item
	 */
	default void doNothingOnFolder(Item item) {}

	/**
	 * @param item
	 */
	default void doNothingOnFile(Item item) {}

	/**
	 * @param destItem
	 * @param name
	 * @return
	 * @throws IOException
	 */
	Item mkdir(Item destItem, String name) throws IOException;

	/**
	 * @param destItem
	 * @param name
	 * @return
	 * @throws IOException
	 */
	Item createFile(Item destItem, String name) throws IOException;

	/**
	 * @param srcItem
	 * @param destItem
	 * @param progress
	 * @throws IOException
	 */
	default void copyForNew(Item srcItem, Item destItem, AtomicLong progress) throws IOException {}

	/**
	 * @param srcItem
	 * @param destItem
	 * @param progress
	 * @throws IOException
	 */
	default void copyForUpdate(Item srcItem, Item destItem, AtomicLong progress) throws IOException {}

	/**
	 * @param item
	 * @return
	 * @throws IOException
	 */
	boolean delete(Item item) throws IOException;

	/**
	 * @param path
	 */
	default void conflict(String path) {}

	/**
	 * @param path
	 */
	default void ignore(String path) {}

	/**
	 * @param path
	 */
	default void unknown(String path) {}
}
