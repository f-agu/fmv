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
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.fagu.fmv.mymedia.sync.FileCount;
import org.fagu.fmv.mymedia.sync.Item;
import org.fagu.fmv.mymedia.sync.Storage;
import org.fagu.fmv.mymedia.sync.Synchronizer;
import org.fagu.fmv.utils.ByteSize;


/**
 * @author f.agu
 */
public class LogSynchronizer extends WrappedSynchronizer {

	private final FileCount scanFileCount;

	private final FileCount syncFileCount;

	private final FileCount deleteFileCount;

	private final PrintStream printStream;

	/**
	 * @param synchronizer
	 * @param printStream
	 */
	public LogSynchronizer(Synchronizer synchronizer, PrintStream printStream) {
		super(synchronizer);
		this.printStream = Objects.requireNonNull(printStream);
		scanFileCount = new FileCount();
		syncFileCount = new FileCount();
		deleteFileCount = new FileCount();
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.impl.WrappedSynchronizer#start(org.fagu.fmv.mymedia.sync.Storage, java.util.List)
	 */
	@Override
	public void start(Storage sourceStorage, List<Storage> destStorages) {
		logPlusSysout("############################################################");
		logPlusSysout("      source: " + sourceStorage);
		logPlusSysout(" destination: " + destStorages.get(0));
		Iterator<Storage> iterator = destStorages.iterator();
		iterator.next();
		while(iterator.hasNext()) {
			logPlusSysout("              " + iterator.next());
		}

		super.start(sourceStorage, destStorages);
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		logPlusSysout("-----------------------------------------------------------");
		logResume("Scanned", scanFileCount);
		logResume("Updated", syncFileCount);
		logResume("Deleted", deleteFileCount);

		super.close();
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#doNothingOnFile(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void doNothingOnFile(Item item) {
		scanFileCount.addFile(item.size());
		super.doNothingOnFile(item);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#doNothingOnFolder(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public void doNothingOnFolder(Item item) {
		scanFileCount.addFolder();
		super.doNothingOnFolder(item);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#mkdir(org.fagu.fmv.mymedia.sync.Item, java.lang.String)
	 */
	@Override
	public Item mkdir(Item destItem, String name) throws IOException {
		log("MKDIR", destItem.toString() + '/' + name);
		Item item = super.mkdir(destItem, name);
		syncFileCount.addFolder();
		return item;
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#copyForNew(org.fagu.fmv.mymedia.sync.Item,
	 *      org.fagu.fmv.mymedia.sync.Item, AtomicLong)
	 */
	@Override
	public void copyForNew(Item srcItem, Item destItem, AtomicLong progress) throws IOException {
		copy("NEW_F", srcItem, destItem);
		super.copyForNew(srcItem, destItem, progress);
		syncFileCount.addFile(srcItem.size());
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#copyForUpdate(org.fagu.fmv.mymedia.sync.Item,
	 *      org.fagu.fmv.mymedia.sync.Item, AtomicLong)
	 */
	@Override
	public void copyForUpdate(Item srcItem, Item destItem, AtomicLong progress) throws IOException {
		copy("SIZE src[" + srcItem.size() + "] != dest[" + destItem.size() + "])", srcItem, destItem);
		super.copyForUpdate(srcItem, destItem, progress);
		syncFileCount.addFile(srcItem.size());
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#delete(org.fagu.fmv.mymedia.sync.Item)
	 */
	@Override
	public boolean delete(Item item) throws IOException {
		if(item == null) {
			return false;
		}
		log("DELETE", item.toString());
		if(super.delete(item)) {
			boolean isFile = item.isFile();
			long size = isFile ? item.size() : 0;
			if(isFile) {
				deleteFileCount.addFile(size);
			} else {
				deleteFileCount.addFolder();
			}
			return true;
		}
		log("ERROR", "Delete failed: " + item);
		return false;
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#conflict(java.lang.String)
	 */
	@Override
	public void conflict(String path) {
		log("CONFLICT", path);
		super.conflict(path);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#ignore(java.lang.String)
	 */
	@Override
	public void ignore(String path) {
		log("IGNORE", path);
		super.ignore(path);
	}

	/**
	 * @see org.fagu.fmv.mymedia.sync.Synchronizer#unknown(java.lang.String)
	 */
	@Override
	public void unknown(String path) {
		log("?", path);
		super.unknown(path);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Log, " + synchronizer.toString();
	}

	// ****************************************************

	/**
	 * @param reason
	 * @param srcItem
	 * @param destItem
	 */
	private void copy(String reason, Item srcItem, Item destItem) {
		String sizeFomat = ByteSize.formatSize(srcItem.size());
		log("COPY", "#" + reason + " " + sizeFomat + " : " + srcItem + " -> " + destItem);
	}

	/**
	 * @param title
	 * @param fileCount
	 */
	private void logResume(String title, FileCount fileCount) {
		logPlusSysout(title + ": " + fileCount.getCountFolders() + " folder(s), " + fileCount.getCountFiles() + " file(s), "
				+ ByteSize.formatSize(fileCount.getFileSize()));

	}

	/**
	 * @param log
	 */
	private void logPlusSysout(String log) {
		log(null, log);
		System.out.println(log);
	}

	/**
	 * @param code
	 * @param log
	 */
	private void log(String code, String log) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = dateFormat.format(new Date());
		StringBuilder buf = new StringBuilder(64);
		buf.append(date).append(' ');
		if(code != null) {
			buf.append(code).append("  "); // StringUtils.rightPad(StringUtils.substring(code, 0, 5), 5)
		}
		buf.append(log);
		printStream.println(buf.toString());
	}

}
