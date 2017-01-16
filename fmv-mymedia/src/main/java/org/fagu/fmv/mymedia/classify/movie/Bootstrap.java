package org.fagu.fmv.mymedia.classify.movie;

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

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.mymedia.classify.Organizer;
import org.fagu.fmv.mymedia.file.MovieFinder;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;
import org.fagu.fmv.utils.file.FileFinderListener;
import org.fagu.fmv.utils.media.Rotation;


/**
 * @author f.agu
 */
public class Bootstrap {

	// ---------------------------------------------------

	/**
	 *
	 */
	private MovieFinder findMovie(File saveFile, File... srcFiles) throws IOException {
		MovieFinder movieFinder = new MovieFinder(saveFile);
		movieFinder.addInfoFile(new VolumeInfoFile());
		movieFinder.addListener(new FileFinderListener<Movie>() {

			private int count;

			/**
			 * @see org.fagu.fmv.utils.file.FileFinderListener#eventFindPre(org.fagu.fmv.utils.file.FileFinder.FileFound)
			 */
			@Override
			public void eventFindPre(FileFound fileFound) {
				System.out.println(count + ": " + fileFound.getFileFound().getName());
				++count;
			}

			/**
			 * @see org.fagu.fmv.utils.file.FileFinderListener#eventFindPost(FileFound, java.lang.Object)
			 */
			@Override
			public void eventFindPost(FileFound fileFound, FileFinder<Movie>.InfosFile infosFile) {
				Movie movie = infosFile.getMain();
				MovieMetadatas videoMetadatas = movie.getMetadatas();

				// Format format = videoMetadatas.getFormat();
				VideoStream videoStream = videoMetadatas.getVideoStream();
				Rotation rotate = videoStream.rotate();
				// System.out.println(count + ": " + file.getName() + "  " + infos); // .getFormat().creationDate()
				System.out.println("   " + rotate + ", " + videoStream.handlerName());
			}
		});

		for(File srcFile : srcFiles) {
			movieFinder.find(srcFile);
		}

		return movieFinder;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File source = new File("C:\\tmp\\bretagne");

		File saveFile = new File(source, "movie.save");
		File destFolder = new File(source.getParentFile(), source.getName() + "-mv-out");

		Bootstrap bootstrap = new Bootstrap();
		try (MovieFinder movieFinder = bootstrap.findMovie(saveFile, source)) {
			Organizer<MovieFinder, Movie> organizer = new Organizer<>(Movie.class);
			organizer.organize(destFolder, movieFinder);
		}
	}
}
