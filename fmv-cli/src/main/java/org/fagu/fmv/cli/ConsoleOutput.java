package org.fagu.fmv.cli;

/*
 * #%L
 * fmv-cli
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

import org.apache.commons.lang.StringUtils;
import org.fagu.fmv.core.project.FileSource;
import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.im.ImageMetadatas;
import org.fagu.fmv.media.FileType;


/**
 * @author f.agu
 */
public class ConsoleOutput {

	/**
	 * 
	 */
	private ConsoleOutput() {}

	/**
	 * @param fileSource
	 * @return
	 */
	public static String forOutput(FileSource fileSource) {
		return forOutput(fileSource, null);
	}

	/**
	 * @param fileSource
	 * @param fromFile
	 * @return
	 */
	public static String forOutput(FileSource fileSource, File fromFile) {
		StringBuilder buf = new StringBuilder(100);
		buf.append(StringUtils.rightPad(Integer.toString(fileSource.getNumber()), 3)).append(' ').append(' ');
		FileType fileType = fileSource.getFileType();
		if(fileType != null) {
			buf.append(fileType.name().charAt(0)).append(' ');
		}
		String meta = null;
		if(fileSource.isImage()) {
			ImageMetadatas imageMetadatas = fileSource.getImageMetadatas();
			if(imageMetadatas != null) {
				meta = imageMetadatas.getDimension().toString();
			}
		}
		if(fileSource.isAudioOrVideo()) {
			MovieMetadatas videoMetadatas = fileSource.getVideoMetadatas();
			if(videoMetadatas != null) {
				AudioStream audioStream = videoMetadatas.getAudioStream();
				VideoStream videoStream = videoMetadatas.getVideoStream();
				if(videoStream != null) {
					meta = videoStream.size().toString() + " " + videoStream.duration();
				} else if(audioStream != null) {
					meta = audioStream.duration().toString();
				}
			}
		}

		String subPath = null;
		if(fromFile != null) {
			subPath = StringUtils.substringAfter(fileSource.getFile().getPath(), fromFile.getPath() + File.separator);
		} else {
			subPath = fileSource.getFile().getName();
		}
		buf.append(StringUtils.rightPad(meta, 25)).append(' ').append(' ').append(subPath);

		return buf.toString();
	}

}
