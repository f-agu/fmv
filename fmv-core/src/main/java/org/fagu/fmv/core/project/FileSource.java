package org.fagu.fmv.core.project;

/*
 * #%L
 * fmv-core
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

import org.fagu.fmv.ffmpeg.FFHelper;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.im.ImageMetadatas;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.Metadatas;


/**
 * @author f.agu
 */
public class FileSource implements Comparable<FileSource> {

	private final FileType fileType;

	private final File file;

	private final int number;

	private MovieMetadatas videoMetadatas;

	private ImageMetadatas imageMetadatas;

	/**
	 * @param fileType
	 * @param file
	 * @param number
	 */
	FileSource(FileType fileType, File file, int number) {
		this.fileType = fileType;
		this.number = number;
		this.file = file;
	}

	/**
	 * @return
	 */
	public FileType getFileType() {
		return fileType;
	}

	/**
	 * @return
	 */
	public boolean isImage() {
		return fileType == FileType.IMAGE;
	}

	/**
	 * @return
	 */
	public boolean isAudio() {
		return fileType == FileType.AUDIO;
	}

	/**
	 * @return
	 */
	public boolean isVideo() {
		return fileType == FileType.VIDEO;
	}

	/**
	 * @return
	 */
	public boolean isAudioOrVideo() {
		return isVideo() || isAudio();
	}

	/**
	 * @return
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @return
	 */
	public File getFile() {
		return file;
	}

	/**
	 *
	 */
	public Metadatas getMetadatas() {
		switch(fileType) {
			case AUDIO:
			case VIDEO:
				return getVideoMetadatas();
			case IMAGE:
				return getImageMetadatas();
			default:
		}
		return null;
	}

	/**
	 * @return
	 */
	public MovieMetadatas getVideoMetadatas() {
		if(videoMetadatas == null && isAudioOrVideo()) {
			try {
				videoMetadatas = FFHelper.videoMetadatas(file);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return videoMetadatas;
	}

	/**
	 * @return
	 */
	public ImageMetadatas getImageMetadatas() {
		if(imageMetadatas == null && isImage()) {
			try {
				imageMetadatas = ImageMetadatas.extract(file);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return imageMetadatas;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(FileSource other) {
		return number - other.number;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return file.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof FileSource)) {
			return false;
		}
		FileSource other = (FileSource)obj;
		return file.equals(other.file);
	}

	// ************************************

	/**
	 * @param imageMetadatas
	 */
	void setImageMetadatas(ImageMetadatas imageMetadatas) {
		this.imageMetadatas = imageMetadatas;
	}

	/**
	 * @param videoMetadatas
	 */
	void setVideoMetadatas(MovieMetadatas videoMetadatas) {
		this.videoMetadatas = videoMetadatas;
	}

}
