package org.fagu.fmv.mymedia.sync.file;

/*
 * #%L
 * fmv-mymedia
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
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.regex.Pattern;

import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.mymedia.sync.Storage;
import org.fagu.fmv.mymedia.sync.StorageFactory;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.utils.PlaceHolder;
import org.fagu.fmv.utils.Replacers;


/**
 * @author f.agu
 */
public class FileStorageFactory extends StorageFactory {

	private static final String NAME = "file";

	/**
	 * 
	 */
	public FileStorageFactory() {
		super(NAME);
	}

	/**
	 * @see org.fagu.sync.StorageFactory#create(NavigableMap, MovieMetadatas, SoftPolicy)
	 */
	@Override
	public Storage create(Properties properties) {
		String path = properties.getProperty("path");
		String pathReplaced = PlaceHolder.with(path).format(Replacers.chain().of(s -> {
			Pattern pattern = Pattern.compile(s);
			for(Entry<String, File> entry : FileUtils.getRootByNameMap().entrySet()) {
				if(pattern.matcher(entry.getKey()).matches()) {
					File value = entry.getValue();
					return value != null ? value.getAbsolutePath() : null;
				}
			}
			return null;
		}).unresolvableCopy());
		File file = new File(pathReplaced == null ? path : pathReplaced);
		return new FileStorage(file);
	}

}
