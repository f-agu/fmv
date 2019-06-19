package org.fagu.fmv.soft.vlc;

import static org.fagu.fmv.soft.find.policy.VersionSoftPolicy.minVersion;

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
import java.util.Properties;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.find.SoftFoundFactory;
import org.fagu.fmv.soft.find.SoftLocator;
import org.fagu.fmv.soft.find.SoftPolicy;
import org.fagu.fmv.soft.find.SoftProvider;
import org.fagu.fmv.soft.find.policy.VersionSoftPolicy;
import org.fagu.fmv.soft.win32.ProgramFilesLocatorSupplier;
import org.fagu.fmv.soft.win32.SoftOnWindows;


/**
 * @author f.agu
 */
public class VLCSoftProvider extends SoftProvider {

	public static final String NAME = "vlc";

	public VLCSoftProvider() {
		this(null);
	}

	public VLCSoftProvider(SoftPolicy softPolicy) {
		super(NAME, ObjectUtils.firstNonNull(softPolicy, new VersionSoftPolicy()
				.onAllPlatforms(minVersion(2, 2))));
	}

	@Override
	public SoftFoundFactory createSoftFoundFactory(Properties searchProperties) {
		if(SystemUtils.IS_OS_WINDOWS) {
			return SoftOnWindows.createSoftFoundFactory(getName());
		}
		throw new RuntimeException("Not implemented"); // TODO
	}

	@Override
	public String getDownloadURL() {
		return "http://www.videolan.org/";
	}

	@Override
	public SoftLocator getSoftLocator() {
		SoftLocator softLocator = super.getSoftLocator();
		if(SystemUtils.IS_OS_WINDOWS) {
			ProgramFilesLocatorSupplier.with(softLocator)
					.findFolder("VideoLAN" + File.separator + "VLC")
					.supplyIn();
			softLocator.addDefaultLocator();
		}
		return softLocator;
	}

}
