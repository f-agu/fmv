package org.fagu.fmv.mymedia.file;

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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.collection.MapList;
import org.fagu.fmv.utils.collection.MultiValueMaps;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FindProgress;


/**
 * @author f.agu
 */
public abstract class AutoSaveLoadFileFinder<T extends Media> extends FileFinder<T> implements Closeable {

	private static final long serialVersionUID = 7384099271785096843L;

	private final File saveFile;

	private final PrintStream printStream;

	private final MediaWithMetadatasInfoFile metadatasInfoFile;

	private final Map<Character, InfoFile> infoFileMap;

	/**
	 * @param extensions
	 * @param bufferSize
	 * @param saveFile
	 * @param metadatasInfoFile
	 */
	public AutoSaveLoadFileFinder(Set<String> extensions, int bufferSize, File saveFile, MediaWithMetadatasInfoFile metadatasInfoFile) {
		super(extensions, bufferSize);
		this.saveFile = saveFile;
		this.metadatasInfoFile = metadatasInfoFile;

		infoFileMap = new LinkedHashMap<>();
		addInfoFile(metadatasInfoFile);

		try {
			printStream = new PrintStream(new FileOutputStream(saveFile, true));
		} catch(FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param infoFile
	 */
	public void addInfoFile(InfoFile infoFile) {
		char code = infoFile.getCode();
		if(code == 'P' || code == 'R' || infoFileMap.containsKey(code)) {
			throw new IllegalArgumentException("InfoFile code '" + code + "' already used");
		}
		infoFileMap.put(code, infoFile);
	}

	/**
	 * @see org.fagu.fmv.utils.file.FileFinder#find(java.util.Collection, org.fagu.fmv.utils.file.FindProgress)
	 */
	@Override
	public int find(Collection<File> files, FindProgress findProgress) {
		load();
		return super.find(files, findProgress);
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		printStream.close();
	}

	// ***********************************************

	/**
	 * @param map
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void afterFlush(Map<FileFound, InfosFile> map) throws IOException {
		MapList<FileFound, String> lines = MultiValueMaps.hashMapArrayList();
		for(Entry<FileFound, InfosFile> entry : map.entrySet()) {
			for(Entry<Character, InfoFile> iFEntry : infoFileMap.entrySet()) {
				InfoFile infoFile = iFEntry.getValue();
				StringBuilder line = new StringBuilder(100);
				FileFinder<T>.InfosFile value = entry.getValue();
				line.append(iFEntry.getKey()).append(' ').append(infoFile.toLine(entry.getKey(), (FileFinder<Media>.InfosFile)value));
				lines.add(entry.getKey(), line.toString());
			}
		}

		for(Entry<FileFound, List<String>> entry : lines.entrySet()) {
			printStream.println("P " + entry.getKey().getFileFound().getAbsolutePath());
			printStream.println("R " + entry.getKey().getRootFolder().getAbsolutePath());
			for(String line : entry.getValue()) {
				printStream.println(line);
			}
		}
		printStream.flush();
	}

	// ***********************************************

	/**
	 *
	 */
	private void load() {
		if( ! saveFile.exists()) {
			return;
		}
		System.out.println("Loading... " + saveFile);
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFile))) {

			File currentRootFolder = null;
			File currentFile = null;
			String line = null;
			int number = 0;
			while((line = bufferedReader.readLine()) != null) {
				++number;
				if(line.charAt(1) != ' ') {
					throw new RuntimeException("Unreadable file (line " + number + ")");
				}
				if(line.startsWith("P ")) {
					currentFile = new File(line.substring(2));
					continue;
				}
				if(currentFile != null && currentFile.exists()) {
					if(line.startsWith("R ")) {
						currentRootFolder = new File(line.substring(2));
						if( ! currentRootFolder.exists()) {
							currentRootFolder = null;
						}
					} else {
						char code = line.charAt(0);
						line = line.substring(2);

						InfoFile infoFile = infoFileMap.get(code);
						if(infoFile == null) {
							throw new RuntimeException("InfoFile code not found: " + code);
						}
						FileFound fileFound = new FileFound(currentRootFolder, currentFile);

						Object info = infoFile.parse(currentFile, line);
						FileFinder<T>.InfosFile infosFile = get(fileFound);
						if(infosFile == null) {
							infosFile = new InfosFile();
							add(fileFound, infosFile);
						}
						if(metadatasInfoFile.isMine(info)) {
							@SuppressWarnings("unchecked")
							T t = (T)info;
							infosFile.setMain(t);
						} else {
							infosFile.addInfo(info);
						}
					}
				}
			}

		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println(count() + " files loaded");
	}
}
