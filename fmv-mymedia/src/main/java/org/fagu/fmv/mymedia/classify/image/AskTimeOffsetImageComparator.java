package org.fagu.fmv.mymedia.classify.image;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import org.fagu.fmv.im.Image;
import org.fagu.fmv.im.ImageMetadatas;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.mymedia.classify.AskTimeOffsetComparator;
import org.fagu.fmv.mymedia.file.ImageFinder;


/**
 * @author f.agu
 */
public class AskTimeOffsetImageComparator extends AskTimeOffsetComparator<Image> implements ImageTimeComparator {

	/**
	 * @param imageFinder
	 */
	public AskTimeOffsetImageComparator(ImageFinder imageFinder) {
		super(imageFinder);
	}

	/**
	 * @param metadatas
	 * @return
	 */
	public String getMetadatasKey(Metadatas metadatas) {
		ImageMetadatas imgMetadatas = (ImageMetadatas)metadatas;
		StringBuilder key = new StringBuilder();
		String device = imgMetadatas.getDevice();
		String deviceModel = imgMetadatas.getDeviceModel();
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
