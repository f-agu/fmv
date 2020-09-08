package org.fagu.fmv.soft.find;

/*-
 * #%L
 * fmv-soft
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
import java.io.IOException;
import java.util.Objects;

import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.win32.BinaryVersionInfo;
import org.fagu.version.Version;


/**
 * @author f.agu
 * @created 16 sept. 2019 17:03:27
 */
public class FileSoftFoundFactory implements SoftFoundFactory {

	private final String softName;

	public FileSoftFoundFactory(String softName) {
		this.softName = Objects.requireNonNull(softName);
	}

	@Override
	public SoftFound create(File file, Locator locator, SoftPolicy softPolicy) throws IOException {
		Version version = BinaryVersionInfo.getFileVersion(file).orElse(null);
		if(version != null) {
			return SoftFound.found(new VersionSoftInfo(file, softName, version));
		}
		return SoftFound.found(file);
	}

}
