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

	public MoveConverter(File destFolder) {
		super(destFolder);
	}

	@Override
	public String getTitle() {
		return "Organiser / deplacer sans reduire";
	}

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

	@Override
	public void close() throws IOException {}

}
