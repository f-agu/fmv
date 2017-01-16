package org.fagu.fmv.mymedia.sync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;

import org.fagu.fmv.mymedia.sync.file.FileStorage;
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
public class SyncAllFTP {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		try (PrintStream printStream = new PrintStream(new FileOutputStream("c:\\tmp\\sync-ftp.log", true))) {

			// photos
			File localPhotos = new File("C:\\Nos Documents\\Photos fagu & Vv");
			try (Storage photoStorage = StorageFactory.create(new File("C:\\Nos Documents\\Commun\\sync\\oodrive-ftp_photos.properties"))) {
				synchronize(printStream, localPhotos, photoStorage);
			}

			// videos
			File localVideos = new File("D:\\Video_fagu&Vv");
			try (Storage videoStorage = StorageFactory.create(new File("C:\\Nos Documents\\Commun\\sync\\oodrive-ftp_videos.properties"))) {
				synchronize(printStream, localVideos, videoStorage);
			}
		}
	}

	/**
	 * @param printStream
	 * @param sourceFile
	 * @param destStorage
	 * @throws IOException
	 */
	private static void synchronize(PrintStream printStream, File sourceFile, Storage destStorage) throws IOException {
		try (Storage sourceStrage = new FileStorage(sourceFile)) {
			Sync sync = new Sync(sourceStrage, Collections.singletonList(destStorage), Synchronizers.getDefault(printStream));
			sync.synchronize();
		}
	}

}
