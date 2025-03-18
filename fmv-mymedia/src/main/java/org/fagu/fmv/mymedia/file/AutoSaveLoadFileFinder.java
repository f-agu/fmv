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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.part.SpinnerPart;
import org.fagu.fmv.textprogressbar.part.SupplierTextPart;
import org.fagu.fmv.textprogressbar.part.TextPart;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FindProgress;


/**
 * @author f.agu
 */
public abstract class AutoSaveLoadFileFinder<T extends Media> extends FileFinder<T> implements Closeable {

	private static final long serialVersionUID = 7384099271785096843L;

	protected final Logger logger;

	private final File saveFile;

	private final PrintStream printStream;

	private final List<InfoFile> infoFiles;

	private final Map<Character, InfoFile> infoFileMap;

	public AutoSaveLoadFileFinder(Logger logger, Set<String> extensions, int bufferSize, File saveFile, List<InfoFile> infoFiles) {
		super(extensions, bufferSize);
		this.logger = logger != null ? logger : Loggers.noOperation();
		this.saveFile = saveFile;
		this.infoFiles = infoFiles != null ? infoFiles : List.of();

		infoFileMap = new LinkedHashMap<>();
		infoFiles.forEach(this::addInfoFile);

		try {
			printStream = new PrintStream(new FileOutputStream(saveFile, true));
		} catch(FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void addInfoFile(InfoFile infoFile) {
		List<Character> codes = infoFile.getCodes();
		for(Character code : codes) {
			if(code == 'P' || code == 'R' || infoFileMap.containsKey(code)) {
				InfoFile inff = infoFileMap.get(code);
				throw new IllegalArgumentException("InfoFile code '" + code + "' already used by "
						+ (inff != null ? inff.getClass().getSimpleName() : "system"));
			}
			infoFileMap.put(code, infoFile);
		}
	}

	@Override
	public int find(Collection<File> files, FindProgress findProgress) {
		if(saveFile.exists()) {
			logger.log("Loading from " + saveFile);
			SupplierTextPart supplierTextPart = new SupplierTextPart();
			try (TextProgressBar textProgressBar = TextProgressBar.newBar()
					.append(new TextPart("Loading...  "))
					.append(new SpinnerPart())
					.append(new TextPart("   "))
					.append(supplierTextPart)
					.autoPrintFull(false)
					.buildAndSchedule()) {
				load(supplierTextPart);
			} catch(IOException e) {
				e.printStackTrace();
			}
			System.out.println();
		}
		return super.find(files, findProgress);
	}

	@Override
	public void close() throws IOException {
		printStream.close();
	}

	// ***********************************************

	protected void showAndLog(String msg) {
		logger.log(msg);
		System.out.println(msg);
	}

	@SuppressWarnings("unchecked")
	protected void afterFlush(Map<FileFound, InfosFile> map) throws IOException {
		Map<FileFound, List<String>> lines = new HashMap<>();
		for(Entry<FileFound, InfosFile> entry : map.entrySet()) {
			for(InfoFile infoFile : infoFileMap.values()) {
				StringBuilder line = new StringBuilder(100);
				FileFinder<T>.InfosFile value = entry.getValue();
				infoFile.toInfo(entry.getKey(), (FileFinder<Media>.InfosFile)value)
						.ifPresent(info -> {
							lines.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
									.add(line.append(info.line().code()).append(' ').append(info.line().value()).toString());
							value.addInfo(info.object());
						});
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

	private void load(SupplierTextPart supplierTextPart) {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFile))) {

			File currentRootFolder = null;
			File currentFile = null;
			String line = null;
			int number = 0;
			int countFiles = 0;
			while((line = bufferedReader.readLine()) != null) {
				++number;
				if(line.charAt(1) != ' ') {
					throw new RuntimeException("Unreadable file (line " + number + ")");
				}
				if(line.startsWith("P ")) {
					supplierTextPart.setText(Integer.toString(++countFiles));
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
						for(InfoFile inff : infoFiles) {
							if(inff.isMine(info)) {
								@SuppressWarnings("unchecked")
								T t = (T)info;
								infosFile.setMain(t);
							} else {
								infosFile.addInfo(info);
							}
						}
					}
				}
			}

		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
