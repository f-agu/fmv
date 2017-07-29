package org.fagu.fmv.soft._7z;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.find.SoftPolicy;


/**
 * @author f.agu
 */
public class _7z {

	/**
	 * 
	 */
	private _7z() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * @return
	 */
	public static Soft search() {
		return Soft.search(new _7zSoftProvider());
	}

	/**
	 * @param softPolicy
	 * @return
	 */
	public static Soft search(SoftPolicy softPolicy) {
		return Soft.search(new _7zSoftProvider(softPolicy));
	}

	/**
	 * @param zipFile
	 * @param destinationFolder
	 * @throws IOException
	 */
	public static void extract(File zipFile, File destinationFolder) throws IOException {
		FileUtils.forceMkdir(destinationFolder);
		search().withParameters("x", zipFile.getAbsolutePath(), "-o" + destinationFolder.getAbsolutePath())
				.execute();
	}

}
