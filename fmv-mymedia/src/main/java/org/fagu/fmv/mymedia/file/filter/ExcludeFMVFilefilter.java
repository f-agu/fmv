package org.fagu.fmv.mymedia.file.filter;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.io.FileFilter;

import org.fagu.fmv.utils.IniFile;


/**
 * @author Utilisateur
 * @created 2 juin 2018 14:54:00
 */
public class ExcludeFMVFilefilter implements FileFilter {

	public static final ExcludeFMVFilefilter INSTANCE = new ExcludeFMVFilefilter();

	private final FileFilter iniFileFilter;

	public ExcludeFMVFilefilter() {
		iniFileFilter = f -> true;
	}

	public ExcludeFMVFilefilter(IniFile iniFile) {
		iniFileFilter = f -> ! iniFile.contains("exclude", f.getName());
	}

	@Override
	public boolean accept(File pathname) {
		return ! pathname.getName().startsWith(".fmv")
				&& iniFileFilter.accept(pathname);
	}

}
