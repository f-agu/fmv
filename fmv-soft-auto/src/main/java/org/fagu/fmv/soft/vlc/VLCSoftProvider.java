package org.fagu.fmv.soft.vlc;

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

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;

import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.SoftOnWindows;
import org.fagu.fmv.soft.find.PlateformFileFilter;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionPolicy;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class VLCSoftProvider extends SoftProvider {

	public static final String NAME = "vlc";

	/**
	 * 
	 */
	public VLCSoftProvider() {
		super(NAME);
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#createSoftFoundFactory()
	 */
	@Override
	public SoftFoundFactory createSoftFoundFactory() {
		if(SystemUtils.IS_OS_WINDOWS) {
			return SoftOnWindows.createSoftFoundFactory(this::getSoftPolicy, this::getSoftName);
		}
		throw new RuntimeException("Not implemented"); // TODO
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getDownloadURL()
	 */
	@Override
	public String getDownloadURL() {
		return "http://www.videolan.org/";
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftLocator()
	 */
	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		FileFilter fileFilter = PlateformFileFilter.getFileFilter(NAME);
		SoftOnWindows.addProgramFilesLocator(softLocator, fileFilter, Collections.singleton("VideoLAN" + File.separator + "VLC"));
		return softLocator;
	}

	/**
	 * @see org.fagu.fmv.soft.find.SoftProvider#getSoftPolicy()
	 */
	@Override
	public SoftPolicy<?, ?, ?> getSoftPolicy() {
		return new VersionPolicy()
				.onAllPlatforms().minVersion(new Version(2, 2));
	}

}