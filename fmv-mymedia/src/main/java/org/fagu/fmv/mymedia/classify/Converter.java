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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public abstract class Converter<M extends Media> implements Closeable {

	protected final File destFolder;

	/**
	 * @param destFolder
	 */
	public Converter(File destFolder) {
		this.destFolder = Objects.requireNonNull(destFolder);
	}

	/**
	 * @param defaultValue
	 * @return
	 */
	public String getFormat(String defaultValue) {
		return defaultValue;
	}

	// ************************************************

	/**
	 * @return
	 */
	abstract public String getTitle();

	/**
	 * @param srcMedia
	 * @param infosFile
	 * @param destFile
	 * @param listener
	 * @throws IOException
	 */
	abstract public void convert(M srcMedia, FileFinder<M>.InfosFile infosFile, File destFile, ConverterListener<M> listener) throws IOException;
}
