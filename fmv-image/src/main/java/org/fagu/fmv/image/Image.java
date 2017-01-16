package org.fagu.fmv.image;

/*
 * #%L
 * fmv-image
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

import org.fagu.fmv.media.Media;


/**
 * @author f.agu
 */
public class Image implements Media, Comparable<Image>, Serializable {

	private static final long serialVersionUID = - 3729180114096861001L;

	private long time;

	private final File file;

	private ImageMetadatas metadatas;

	/**
	 * @param file
	 */
	public Image(File file) {
		this.file = file;
	}

	/**
	 * @param file
	 * @param metadatas
	 */
	public Image(File file, ImageMetadatas metadatas) {
		this.file = file;
		this.metadatas = metadatas;
	}

	/**
	 * @see org.fagu.fmv.mymedia.Media#getFile()
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Image o) {
		long t1 = getTime();
		long t2 = o.getTime();
		if(t1 < t2) {
			return - 1;
		}
		if(t1 > t2) {
			return 1;
		}
		return file.getName().compareToIgnoreCase(o.file.getName());
	}

	/**
	 * @see org.fagu.fmv.media.Media#getTime()
	 */
	public long getTime() {
		if(time > 0) {
			return time;
		}
		return time = getMetadatas().getDate().getTime();
	}

	/**
	 * @see org.fagu.fmv.mymedia.Media#getMetadatas()
	 */
	public ImageMetadatas getMetadatas() {
		if(metadatas == null) {
			try {
				metadatas = ImageMetadatas.extract(file);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return metadatas;
	}

}
