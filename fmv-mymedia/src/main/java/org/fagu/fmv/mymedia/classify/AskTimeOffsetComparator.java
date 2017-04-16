package org.fagu.fmv.mymedia.classify;

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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public abstract class AskTimeOffsetComparator<M extends Media> implements MediaTimeComparator<M> {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private final FileFinder<M> fileFinder;

	/**
	 * Map<metadatas, offset>
	 */
	private Map<String, Long> metadatasMap = new LinkedHashMap<>();

	/**
	 * @param fileFinder
	 */
	public AskTimeOffsetComparator(FileFinder<M> fileFinder) {
		this.fileFinder = Objects.requireNonNull(fileFinder);
	}

	/**
	 * @param filter
	 * @param timeDiff
	 */
	public void addMetadatas(String metadatas, long timeDiff) {
		metadatasMap.put(metadatas, timeDiff);
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.MediaTimeComparator#getTime(org.fagu.fmv.media.Media)
	 */
	@Override
	public long getTime(M media) {
		String metadatasKey = media.getDevice();
		Long offset = metadatasMap.get(metadatasKey);
		if(offset == null) {
			offset = askTimeOffset(metadatasKey);
			metadatasMap.put(metadatasKey, offset);
		}
		return media.getTime() + offset;
	}

	// *************************************************************
	/**
	 * @param key
	 * @return
	 */
	private long askTimeOffset(String key) {
		SortedSet<M> images = new TreeSet<>((m1, m2) -> m1.getFile().compareTo(m2.getFile()));
		for(FileFinder<M>.InfosFile infosFile : fileFinder.getAll()) {
			M media = infosFile.getMain();
			if(key.equals(media.getDevice())) {
				images.add(media);
			}
		}
		displayAllFilesFromSameDevice(images, null);
		long offset = 0;
		do {
			offset = scan(key);
			if(offset != 0) {
				displayAllFilesFromSameDevice(images, offset);
			}
		} while( ! scanYesOrNo());

		return offset;
	}

	/**
	 * @return
	 */
	private long scan(String key) {
		Scanner scanner = new Scanner(System.in);
		while(true) {
			System.out.println(key);
			System.out.print("Diff time in minute ? ");
			String nextLine = scanner.nextLine();
			try {
				return Long.parseLong(nextLine) * 60L * 1000L;
			} catch(Exception e) {
				//
			}
		}

	}

	/**
	 * @return
	 */
	private boolean scanYesOrNo() {
		return true;
	}

	/**
	 * @return
	 */
	private boolean scanYesOrNo_legacy() {
		System.out.print("[y/n] ");
		while(true) {
			try {
				char c = (char)System.in.read();
				// int read = console.reader().read();
				if('Y' == c || 'y' == c) {
					return true;
				}
				if('N' == c || 'n' == c) {
					return false;
				}
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @param medias
	 * @param offset
	 */
	private void displayAllFilesFromSameDevice(Collection<M> medias, Long offset) {
		for(M media : medias) {
			StringBuilder line = new StringBuilder(50);
			long time = media.getTime();
			line.append(DATE_FORMAT.format(new Date(time)));
			if(offset != null) {
				line.append(" -> ").append(DATE_FORMAT.format(new Date(time + offset)));
			}
			line.append(' ').append(media.getFile());
			System.out.println(line);
		}
	}
}
