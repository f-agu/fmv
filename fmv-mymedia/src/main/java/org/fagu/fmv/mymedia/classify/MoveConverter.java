package org.fagu.fmv.mymedia.classify;

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

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public class MoveConverter extends Converter<Media> {

	/**
	 * @param destFolder
	 */
	public MoveConverter(File destFolder) {
		super(destFolder);
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.Converter#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Organiser / deplacer sans reduire";
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.Converter#convert(org.fagu.fmv.media.Media,
	 *      org.fagu.fmv.utils.file.FileFinder.InfosFile, java.io.File, org.fagu.fmv.mymedia.classify.ConverterListener)
	 */
	@Override
	public void convert(Media srcMedia, FileFinder<Media>.InfosFile infosFile, File destFile, ConverterListener<Media> listener) throws IOException {
		if(listener != null) {
			listener.eventPreConvert(srcMedia, destFile);
		}
		srcMedia.getFile().renameTo(destFile);
		if(listener != null) {
			listener.eventPostConvert(srcMedia, destFile);
		}
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {}

}
