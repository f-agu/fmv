package org.fagu.fmv.core.project;

/*
 * #%L
 * fmv-core
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

import org.fagu.fmv.core.exec.Executable;


/**
 * @author f.agu
 */
public interface ProjectListener {

	// ******* SOURCE *******

	/**
	 * @param fromFile
	 * @param fileSource
	 * @param index
	 */
	default void eventAddSourcePre(File fromFile, FileSource fileSource, int index) {}

	/**
	 * @param fromFile
	 * @param fileSource
	 * @param index
	 */
	default void eventAddSourcePost(File fromFile, FileSource fileSource, int index) {}

	// ******* EXEC PREVIEW *******

	/**
	 * @param executable
	 */
	default void eventExecPrePreviewViaMake(Executable executable) {}

	/**
	 * @param executable
	 * @param destinationFile
	 */
	default void eventExecPrePreviewScale(Executable executable, File destinationFile) {}

	/**
	 * @param executable
	 * @param destinationFile
	 */
	default void eventExecPostPreviewScale(Executable executable, File destinationFile) {}
}
