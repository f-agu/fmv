package org.fagu.fmv.mymedia.sync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.fagu.fmv.mymedia.sync.file.FileStorage;
import org.fagu.fmv.mymedia.sync.file.FileUtils;
import org.fagu.fmv.mymedia.sync.impl.Synchronizers;


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

/**
 * @author f.agu
 */
public class SyncAllLocal {

	/**
	 * 
	 */
	public SyncAllLocal() {}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		File localPhotos = new File("C:\\Nos Documents\\Photos fagu & Vv");
		File localVideos = new File("D:\\Video_fagu&Vv");
		File faguVv1 = FileUtils.getRootByName("fagu_Vv_1");
		File faguVv2 = FileUtils.getRootByName("fagu_Vv_2");

		try (PrintStream printStream = new PrintStream(new FileOutputStream("c:\\tmp\\sync.log", true))) {
			List<File> hdRoots = new ArrayList<>(2);
			if(exists(faguVv1)) {
				hdRoots.add(faguVv1);
			}
			if(exists(faguVv2)) {
				hdRoots.add(faguVv2);
			}

			if( ! hdRoots.isEmpty()) {
				syncLocalToHD(printStream, localPhotos, localVideos, hdRoots);
			}

			if(hdRoots.size() == 2) {
				syncBothHD(printStream, faguVv1, faguVv2);
			}
		}
	}

	/**
	 * @param file
	 * @return
	 */
	private static boolean exists(File file) {
		return file != null && file.exists();
	}

	/**
	 * @param localPhotos
	 * @param localVideos
	 * @param faguVvx
	 */
	private static void syncLocalToHD(PrintStream printStream, File localPhotos, File localVideos, List<File> faguVvxs) throws IOException {
		// photos
		synchronize(printStream, localPhotos, getFoldersInHD(faguVvxs, "Photos fagu & Vv"));
		// videos
		synchronize(printStream, localVideos, getFoldersInHD(faguVvxs, "Video_fagu&Vv"));
	}

	/**
	 * @param faguVvx
	 * @param folderName
	 * @return
	 */
	private static List<File> getFoldersInHD(List<File> faguVvx, String folderName) {
		return faguVvx.stream().map(f -> new File(f, folderName)).collect(Collectors.toList());
	}

	/**
	 * @param faguVv1
	 * @param faguVv2
	 */
	private static void syncBothHD(PrintStream printStream, File faguVv1, File faguVv2) throws IOException {
		// no photos and no videos, already synchronized with local

		List<String> folders = new ArrayList<>();
		folders.add("Dessins animés");
		folders.add("Dessins animés série");
		folders.add("Documentaires");
		folders.add("Films");
		folders.add("Films HD");
		folders.add("Hapkido");
		folders.add("Oodrive");
		folders.add("Séries");
		folders.add("Soft");

		for(String folder : folders) {
			synchronize(printStream, new File(faguVv2, folder), Collections.singletonList(new File(faguVv1, folder)));
		}
	}

	/**
	 * @param printStream
	 * @param sourceFile
	 * @param destFiles
	 * @throws IOException
	 */
	private static void synchronize(PrintStream printStream, File sourceFile, List<File> destFiles) throws IOException {
		List<Storage> destStorages = null;
		try (Storage sourceStrage = new FileStorage(sourceFile)) {
			destStorages = destFiles.stream().map(f -> new FileStorage(f)).collect(Collectors.toList());
			Synchronizer synchronizer = Synchronizers.getDefault(printStream);
			Sync sync = new Sync(sourceStrage, destStorages, synchronizer);
			sync.synchronize();
		} finally {
			if(destStorages != null) {
				for(Storage storage : destStorages) {
					try {
						storage.close();
					} catch(IOException ignore) {}
				}
			}
		}
	}
}
