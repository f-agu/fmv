package org.fagu.fmv.mymedia.reduce;

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
import java.io.IOException;


/**
 * @author f.agu
 */
public abstract class AbstractReducer implements Reducer {

	/**
	 * 
	 */
	public AbstractReducer() {}

	// ***************************************

	/**
	 * @param srcFile
	 * @param extension
	 * @return
	 */
	protected File getTempFile(File srcFile, String extension) throws IOException {
		String name = srcFile.getName();
		return File.createTempFile(name, '.' + extension, srcFile.getParentFile());
	}

}
