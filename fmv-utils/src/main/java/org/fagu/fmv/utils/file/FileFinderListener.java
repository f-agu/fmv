package org.fagu.fmv.utils.file;

/*
 * #%L
 * fmv-utils
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

import org.fagu.fmv.utils.file.FileFinder.FileFound;


/**
 * @author f.agu
 */
public interface FileFinderListener<T> {

	/**
	 * @param fileFound
	 */
	default void eventFindPre(FileFound fileFound) {}

	/**
	 * @param fileFound
	 * @param infosFile
	 */
	default void eventFindPost(FileFound fileFound, FileFinder<T>.InfosFile infosFile) {}
}
