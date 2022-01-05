package org.fagu.fmv.soft.win32;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
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

import org.fagu.fmv.soft.find.Lines;
import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.version.Version;


/**
 * @author fagu
 */
public class SoftOnWindows {

	private SoftOnWindows() {}

	public static SoftFoundFactory createSoftFoundFactory(String softName) {
		return (file, locator, softPolicy) -> {
			try {
				String versionStr = SoftOnWindows.getExeVersion(file);
				Version version = Version.parse(versionStr);
				return softPolicy.toSoftFound(new VersionSoftInfo(file, softName, version), new Lines());
			} catch(Error e) {
				return SoftFound.foundError(file, e.toString());
			}
		};
	}

	public static String getExeVersion(File file) {
		return WindowsFileVersionInfo.getVersion(file.getAbsolutePath());
	}

}
