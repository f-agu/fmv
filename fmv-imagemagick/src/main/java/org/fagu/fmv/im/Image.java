package org.fagu.fmv.im;

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

	private IMIdentifyImageMetadatas metadatas;

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
	public Image(File file, IMIdentifyImageMetadatas metadatas) {
		this.file = file;
		this.metadatas = metadatas;
	}

	/**
	 * @see org.fagu.fmv.mymedia.Media#getFile()
	 */
	@Override
	public File getFile() {
		return file;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
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
	@Override
	public long getTime() {
		if(time > 0) {
			return time;
		}
		time = getMetadatas().getDate().toInstant().toEpochMilli();
		return time;
	}

	/**
	 * @see org.fagu.fmv.mymedia.Media#getMetadatas()
	 */
	@Override
	public IMIdentifyImageMetadatas getMetadatas() {
		if(metadatas == null) {
			try {
				metadatas = IMIdentifyImageMetadatas.with(file).extract();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return metadatas;
	}

	/**
	 * @see org.fagu.fmv.media.Media#getDevice()
	 */
	@Override
	public String getDevice() {
		StringBuilder key = new StringBuilder();
		String device = metadatas.getDevice();
		String deviceModel = metadatas.getDeviceModel();
		if(device != null) {
			key.append(device);
			if(deviceModel != null) {
				key.append('/');
			}
		}
		if(deviceModel != null) {
			key.append(deviceModel);
		}
		return key.toString();

	}

}
