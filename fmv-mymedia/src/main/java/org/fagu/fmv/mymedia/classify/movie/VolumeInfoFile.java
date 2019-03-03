package org.fagu.fmv.mymedia.classify.movie;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import java.util.OptionalInt;

import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.VolumeDetect;
import org.fagu.fmv.ffmpeg.filter.impl.VolumeDetected;
import org.fagu.fmv.ffmpeg.format.NullMuxer;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.ffmpeg.progressbar.FFMpegProgressBar;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.mymedia.file.InfoFile;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.FileFound;


/**
 * @author f.agu
 */
public class VolumeInfoFile implements InfoFile {

	/**
	 * @see org.fagu.fmv.mymedia.file.InfoFile#getCode()
	 */
	@Override
	public char getCode() {
		return 'V';
	}

	/**
	 * @see org.fagu.fmv.mymedia.file.InfoFile#isMine(java.lang.Object)
	 */
	@Override
	public boolean isMine(Object object) {
		return object instanceof VolumeDetected;
	}

	/**
	 * @see org.fagu.fmv.mymedia.file.InfoFile#toLine(org.fagu.fmv.utils.file.FileFinder.FileFound,
	 *      org.fagu.fmv.media.Media)
	 */
	@Override
	public String toLine(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException {
		Media main = infosFile.getMain();
		MovieMetadatas metadatas = (MovieMetadatas)main.getMetadatas();
		if(metadatas == null || metadatas.getAudioStreams().isEmpty()) {
			return "";
		}

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.addMediaInputFile(fileFound.getFileFound());

		VolumeDetect volumeDetect = VolumeDetect.build();
		builder.filter(volumeDetect);

		builder.addMediaOutput(NullMuxer.build()).overwrite();

		FFExecutor<Object> executor = builder.build();
		OptionalInt countEstimateFrames = metadatas.getVideoStream().countEstimateFrames();
		Progress progress = executor.getProgress();
		if(countEstimateFrames.isPresent() && progress != null) {
			try (TextProgressBar bar = FFMpegProgressBar.with(progress)
					.byFrame(countEstimateFrames.getAsInt())
					.build()
					.makeBar("Detect volume")) {
				executor.execute();
			}
			System.out.println();
		}
		if(volumeDetect.isDetected()) {
			VolumeDetected volumeDetected = volumeDetect.getDetected();
			return volumeDetected.toString();
		}
		return "";
	}

	/**
	 * @see org.fagu.fmv.mymedia.file.InfoFile#parse(java.io.File, java.lang.String)
	 */
	@Override
	public Object parse(File file, String line) throws IOException {
		return VolumeDetected.parse(line);
	}

}
