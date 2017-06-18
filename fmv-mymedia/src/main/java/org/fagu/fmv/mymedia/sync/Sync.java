package org.fagu.fmv.mymedia.sync;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.mymedia.utils.TextProgressBar;
import org.fagu.fmv.utils.ByteSize;
import org.fagu.fmv.utils.IniFile;


/**
 * @author f.agu
 */
public class Sync {

	private final Storage sourceStorage;

	private final List<Storage> destStorages;

	private final Synchronizer synchronizer;

	/**
	 * @param sourceStorage
	 * @param destStorage
	 * @param synchronizer
	 */
	public Sync(Storage sourceStorage, Storage destStorage, Synchronizer synchronizer) {
		this(sourceStorage, Collections.singletonList(destStorage), synchronizer);
	}

	/**
	 * @param sourceStorage
	 * @param destStorages
	 * @param printStream
	 */
	public Sync(Storage sourceStorage, List<Storage> destStorages, Synchronizer synchronizer) {
		if(destStorages.isEmpty()) {
			throw new IllegalArgumentException("Destination empty");
		}
		this.sourceStorage = Objects.requireNonNull(sourceStorage);
		this.destStorages = Objects.requireNonNull(destStorages);
		this.synchronizer = Objects.requireNonNull(synchronizer);
	}

	/**
	 * @throws Exception
	 */
	public void synchronize() throws IOException {
		System.out.println("Synchronizer: " + synchronizer);
		synchronizer.start(sourceStorage, destStorages);
		Item srcRoot = sourceStorage.getRoot();
		List<Item> destRoots = new ArrayList<>(destStorages.size());
		for(Storage destStorage : destStorages) {
			destRoots.add(destStorage.getRoot());
		}
		sync(srcRoot, destRoots);
		synchronizer.close();
	}

	// **************************************************

	// --------------------------------------------

	/**
	 * @author f.agu
	 */
	private class Dest {

		private final Item item;

		private final Map<String, Item> childMap;

		private final IniFile iniFile;

		private Dest(Item item, Map<String, Item> childMap, IniFile iniFile) {
			this.item = item;
			this.childMap = childMap;
			this.iniFile = iniFile;
		}
	}

	// --------------------------------------------

	/**
	 * @param srcItem
	 * @param destItemsList
	 * @throws IOException
	 */
	private void sync(Item srcItem, List<Item> destItemsList) throws IOException {
		System.out.println(srcItem.toString());
		synchronizer.doNothingOnFolder(srcItem);
		Map<String, Item> listChildren = srcItem.listChildren();

		List<Dest> dests = new ArrayList<>();
		for(Item destItem : destItemsList) {
			Map<String, Item> map = destItem.listChildren();
			dests.add(new Dest(destItem, map, loadInitFile(map)));
		}
		IniFile srcInitFile = loadInitFile(listChildren);
		for(Entry<String, Item> entry : listChildren.entrySet()) {
			String name = entry.getKey();
			Item curSrcItem = entry.getValue();

			List<Item> curDestItems = new ArrayList<>();
			for(Dest dest : dests) {
				Item destItem = dest.item;
				Map<String, Item> destItems = dest.childMap;
				IniFile destInitFile = dest.iniFile;
				Item curDestItem = destItems.remove(name);
				if(curDestItem != null) {
					curDestItems.add(curDestItem);
				}
				if( ! accept(curSrcItem, srcInitFile)) {
					synchronizer.ignore(curSrcItem.toString());
					continue;
				}
				if(curDestItem != null && ! accept(curDestItem, destInitFile)) {
					synchronizer.ignore(curDestItem.toString());
					continue;
				}
				if(curDestItem == null) {
					if(curSrcItem.isDirectory()) {
						Item ditem = synchronizer.mkdir(destItem, curSrcItem.getName());
						if(ditem != null) {
							sync(curSrcItem, Collections.singletonList(ditem));
						}
					} else if(curSrcItem.isFile()) {
						curDestItem = synchronizer.createFile(destItem, curSrcItem.getName());
						if(curDestItem != null) {
							final Item finalCurDestItem = curDestItem;
							progressBar(progress -> synchronizer.copyForNew(curSrcItem, finalCurDestItem, progress), curSrcItem);
						}
					} else {
						synchronizer.unknown(srcItem.toString());
					}
				} else if(curDestItem.isFile() != curSrcItem.isFile() || curDestItem.isDirectory() != curSrcItem.isDirectory()) {
					synchronizer.conflict(curDestItem.toString());
					// conflict type
				} else if(curDestItem.isDirectory()) {
					// sync(curSrcItem, Collections.singletonList(curDestItem));
				} else if(curDestItem.size() != curSrcItem.size()) {
					// to update
					final Item finalCurDestItem = curDestItem;
					progressBar(progress -> synchronizer.copyForUpdate(curSrcItem, finalCurDestItem, progress), curSrcItem);
				}
				if(curSrcItem.isFile()) {
					synchronizer.doNothingOnFile(curDestItem);
				}
			}
			if(curSrcItem.isDirectory()) {
				sync(curSrcItem, curDestItems);
			}
		}

		for(Dest dest : dests) {
			for(Entry<String, Item> entry : dest.childMap.entrySet()) {
				Item value = entry.getValue();
				// to delete ?
				if( ! accept(value, dest.iniFile)) {
					synchronizer.ignore(value.toString());
					continue;
				}
				synchronizer.delete(value);
			}
		}
	}

	// ------------------------------------

	/**
	 * @author fagu
	 *
	 * @param <T>
	 */
	@FunctionalInterface
	private static interface ProgressConsumer {

		/**
		 * @param t
		 * @throws IOException
		 */
		void progress(AtomicLong progress) throws IOException;

	}

	// ------------------------------------

	/**
	 * @param progressConsumer
	 * @param srcItem
	 * @throws IOException
	 */
	private void progressBar(ProgressConsumer progressConsumer, Item srcItem) throws IOException {
		AtomicLong progress = new AtomicLong();
		long startTime = System.currentTimeMillis();
		final int width = 30;
		try (TextProgressBar progressBar = TextProgressBar.width(40)
				.consolePrefixMessage(() -> {
					StringBuilder buf = new StringBuilder();
					buf.append(StringUtils.rightPad(StringUtils.abbreviate(srcItem.getName(), width), width)).append("   ");
					long diffTime = System.currentTimeMillis() - startTime;
					String speedText = "";
					if(diffTime > 0) {
						long speed = (1000L * progress.get()) / diffTime;
						speedText = ByteSize.formatSize(speed) + "/s";
					}
					buf.append(StringUtils.rightPad(speedText, 10));
					return buf.toString();
				})
				.buildForScheduling(() -> (int)((100L * progress.get()) / srcItem.size()))
				// .estimatedTimeOfArrival(etaInSeconds) TODO
				.schedule()) {
			progressConsumer.progress(progress);
		}
		System.out.println();
	}

	/**
	 * @param children
	 * @return
	 * @throws IOException
	 */
	private IniFile loadInitFile(Map<String, Item> children) throws IOException {
		Item item = children.get(".fmv-sync");
		if(item == null) {
			return null;
		}
		try (InputStream inputStream = item.openInputStream()) {
			return IniFile.parse(inputStream);
		}
	}

	/**
	 * @param item
	 * @param iniFile
	 * @return
	 */
	private boolean accept(Item item, IniFile iniFile) {
		String name = item.getName();
		if(name.startsWith(".fmv-")) {
			return false;
		}
		if(iniFile == null) {
			return true;
		}
		return ! iniFile.contains("exclude", name);
	}

}
