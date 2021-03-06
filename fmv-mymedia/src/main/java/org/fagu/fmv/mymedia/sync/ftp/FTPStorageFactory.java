package org.fagu.fmv.mymedia.sync.ftp;

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

import java.util.NavigableMap;
import java.util.Properties;

import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.mymedia.sync.Storage;
import org.fagu.fmv.mymedia.sync.StorageFactory;
import org.fagu.fmv.soft.find.SoftPolicy;


/**
 * @author f.agu
 */
public class FTPStorageFactory extends StorageFactory {

	private static final String NAME = "ftp";

	/**
	 * 
	 */
	public FTPStorageFactory() {
		super(NAME);
	}

	/**
	 * @see org.fagu.sync.StorageFactory#create(NavigableMap, MovieMetadatas, SoftPolicy)
	 */
	@Override
	public Storage create(Properties properties) {
		String host = properties.getProperty("host");
		String login = properties.getProperty("login");
		String password = properties.getProperty("password");
		String path = properties.getProperty("path");
		try {
			return new FTPStorage(host, login, password, path);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
