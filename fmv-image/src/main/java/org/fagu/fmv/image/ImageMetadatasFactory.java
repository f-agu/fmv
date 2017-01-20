package org.fagu.fmv.image;

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.image.exception.ImageExceptionKnown;

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

import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.media.MetadatasFactory;


/**
 * @author f.agu
 */
public class ImageMetadatasFactory extends MetadatasFactory {

	/**
	 * 
	 */
	public ImageMetadatasFactory() {
		super(ImageExceptionKnown.class);
	}

	/**
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(FileType t) {
		return t == FileType.IMAGE;
	}

	/**
	 * @see org.fagu.fmv.media.MetadatasFactory#extract(java.io.File)
	 */
	@Override
	public Metadatas extract(File file) throws IOException {
		return ImageMetadatas.extract(file);
	}

	/**
	 * @see org.fagu.fmv.media.MetadatasFactory#parseJSON(java.lang.String)
	 */
	@Override
	public Metadatas parseJSON(String json) {
		return ImageMetadatas.parseJSON(json);
	}

}