package org.fagu.fmv.mymedia.file;

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
import org.fagu.fmv.utils.file.FileFinder.FileFound;


/**
 * @author f.agu
 */
public interface InfoFile {

	/**
	 * @return
	 */
	char getCode();

	/**
	 * @param object
	 * @return
	 */
	boolean isMine(Object object);

	/**
	 * @param fileFound
	 * @param media
	 * @return
	 * @throws IOException
	 */
	String toLine(FileFound fileFound, FileFinder<Media>.InfosFile infosFile) throws IOException;

	/**
	 * @param file
	 * @param line
	 * @return
	 * @throws IOException
	 */
	Object parse(File file, String line) throws IOException;
}
