package org.fagu.fmv.utils.file;

/*
 * #%L
 * fmv-utils
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.utils.Proxifier;


/**
 * @author f.agu
 */
public abstract class FileFinder<T> implements Serializable {

	private static final long serialVersionUID = - 6555092287035209202L;

	private static final int BUFFER_SIZE = 20;

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class FileFound implements Comparable<FileFound> {

		private final File rootFolder;

		private final File fileFound;

		public FileFound(File rootFolder, File fileFound) {
			this.rootFolder = rootFolder;
			this.fileFound = fileFound;
		}

		public File getFileFound() {
			return fileFound;
		}

		public File getRootFolder() {
			return rootFolder;
		}

		@Override
		public int compareTo(FileFound o) {
			return fileFound.compareTo(o.fileFound);
		}

		@Override
		public int hashCode() {
			return fileFound.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof FileFound) {
				return fileFound.equals(((FileFound)obj).fileFound);
			}
			return false;
		}

		@Override
		public String toString() {
			return fileFound.toString();
		}
	}

	// -----------------------------------------------

	/**
	 * @author f.agu
	 */
	public class InfosFile {

		private T mainInfo;

		private final List<Object> infos;

		public InfosFile() {
			this.infos = new ArrayList<>();
		}

		public InfosFile(T mainInfo, Object... infos) {
			this.mainInfo = mainInfo;
			this.infos = new ArrayList<>(Arrays.asList(infos));
		}

		public InfosFile(T mainInfo, Collection<Object> infos) {
			this.mainInfo = mainInfo;
			this.infos = new ArrayList<>(infos);
		}

		public T getMain() {
			return mainInfo;
		}

		public void setMain(T mainInfo) {
			this.mainInfo = mainInfo;
		}

		public void addInfo(Object object) {
			if(object != null) {
				infos.add(object);
			}
		}

		public List<Object> getInfos() {
			return infos;
		}
	}

	// -----------------------------------------------

	private final int bufferSize;

	private final Set<String> extensions;

	private final List<FileFound> bufferedList;

	private final Map<FileFound, InfosFile> fileMap;

	private transient List<FileFinderListener<T>> listeners;

	private FileFinderListener<T> fileFinderListener;

	private List<Future<Map<FileFound, InfosFile>>> futures = new ArrayList<>();

	public FileFinder(Set<String> extensions) {
		this(extensions, BUFFER_SIZE);
	}

	public FileFinder(Set<String> extensions, int bufferSize) {
		this.extensions = extensions;
		this.bufferSize = Math.min(100, Math.max(1, bufferSize));
		fileMap = new HashMap<>();
		listeners = new ArrayList<>();
		bufferedList = new ArrayList<>(bufferSize);
	}

	public void addListener(FileFinderListener<T> listener) {
		if(listener != null) {
			listeners.add(listener);
		}
	}

	public int count() {
		return fileMap.size();
	}

	public int find(File file) {
		return find(Collections.singletonList(file), null);
	}

	public int find(File file, FindProgress findProgress) {
		return find(Collections.singletonList(file), findProgress);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public int find(Collection<File> files, FindProgress findProgress) {
		Proxifier<FileFinderListener> proxifier = new Proxifier<>(FileFinderListener.class);
		fileFinderListener = proxifier.addAll(listeners).proxify();

		IntConsumer atEndExt = null;
		if(findProgress != null) { // with progress
			findProgress.start();
			AtomicInteger currentCount = new AtomicInteger();
			findProgress.progress("Counting files...");
			int countTotal = files.stream()
					.mapToInt(file -> find(
							file,
							file,
							(rf, f) -> {
								if(accept(f)) {
									findProgress.progress("Counting files...  " + currentCount.incrementAndGet() + " files found");
									return true;
								}
								return false;
							},
							null)).sum();
			AtomicInteger doneCount = new AtomicInteger();
			final IntConsumer atEndM = count -> {
				flushBuffer(l -> {
					doneCount.addAndGet(l.size());
					int done = doneCount.get();
					if(done > 0 && countTotal > 0) {
						findProgress.progress(done, countTotal);
					}
				});
			};
			atEndExt = atEndM;

		} else { // without progress
			IntConsumer atEndM = count -> flushBuffer(null);
			atEndExt = atEndM;
		}

		final IntConsumer atEnd = atEndExt;
		final BiFunction<File, File, Boolean> addFunction = (rf, f) -> add(rf, f, atEnd);
		int count = files.stream().mapToInt(file -> find(file, file, addFunction, atEnd)).sum();

		// wait the end of all futures
		// System.out.println("futures: " + futures.size());
		for(Future<Map<FileFound, InfosFile>> future : futures) {
			try {
				future.get(1, TimeUnit.HOURS);
			} catch(InterruptedException | ExecutionException | TimeoutException e) {
				throw new RuntimeException(e);
			}
		}
		// System.out.println(" ALL DONES: " + futures.size());

		return count;
	}

	public Set<FileFound> getSources() {
		return fileMap.keySet();
	}

	public Collection<InfosFile> getAll() {
		return Collections.unmodifiableCollection(fileMap.values());
	}

	public Map<FileFound, InfosFile> getAllMap() {
		return Collections.unmodifiableMap(fileMap);
	}

	public InfosFile get(FileFound source) {
		return fileMap.get(source);
	}

	public boolean contains(FileFound source) {
		return fileMap.containsKey(source);
	}

	// *****************************************

	abstract protected Future<Map<FileFound, InfosFile>> flushToMap(List<FileFound> buffer, Consumer<List<FileFound>> consumer);

	// *****************************************

	protected void add(FileFound file, InfosFile infosFile) {
		fileMap.put(file, infosFile);
	}

	protected void addAll(Map<FileFound, InfosFile> map) {
		for(Entry<FileFound, InfosFile> entry : map.entrySet()) {
			fileMap.put(entry.getKey(), entry.getValue());
			fileFinderListener.eventFindPost(entry.getKey(), entry.getValue());
		}
	}

	// *****************************************

	private boolean accept(File file) {
		String name = file.getName();
		String extension = FilenameUtils.getExtension(name);
		if(extension == null) {
			return false;
		}
		extension = extension.toLowerCase();
		return extensions.contains(extension);
	}

	private boolean add(File rootFolder, File file, IntConsumer atEnd) {
		if( ! accept(file)) {
			return false;
		}
		FileFound fileFound = new FileFound(rootFolder, file);
		if(fileMap.containsKey(fileFound)) {
			return false;
		}
		fileFinderListener.eventFindPre(fileFound);
		bufferedList.add(fileFound);
		if(bufferedList.size() == bufferSize) {
			if(atEnd != null) {
				atEnd.accept(1);
			}
		}
		return true;
	}

	private int find(File rootFolder, File file, BiFunction<File, File, Boolean> addFunction, IntConsumer atEnd) {
		if(file.isFile()) {
			return addFunction.apply(rootFolder, file) ? 1 : 0;
		}
		File[] childs = file.listFiles();
		if(childs == null) {
			return 0;
		}
		int count = 0;
		for(File child : childs) {
			if(child.isDirectory()) {
				count += find(rootFolder, child, addFunction, atEnd);
			} else if(child.isFile() && ! contains(new FileFound(rootFolder, child))) {
				if(addFunction.apply(rootFolder, child)) {
					++count;
				}
			}
		}
		if(atEnd != null) {
			atEnd.accept(count);
		}
		return count;
	}

	private void flushBuffer(Consumer<List<FileFound>> consumer) {
		Future<Map<FileFound, InfosFile>> future = flushToMap(new ArrayList<>(bufferedList), consumer);
		if( ! future.isDone()) {
			futures.add(future);
		} else {
			Map<FileFound, InfosFile> map;
			try {
				map = future.get(1, TimeUnit.SECONDS);
			} catch(InterruptedException | ExecutionException | TimeoutException e) {
				throw new RuntimeException(e);
			}
			for(Entry<FileFound, InfosFile> entry : map.entrySet()) {
				fileMap.put(entry.getKey(), entry.getValue());
				fileFinderListener.eventFindPost(entry.getKey(), entry.getValue());
			}
		}
		bufferedList.clear();
	}

}
