package org.fagu.fmv.ffmpeg.soft;

/*
 * #%L
 * fmv-ffmpeg
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

import java.util.Date;
import java.util.Objects;

import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class FFSoftPolicy {

	public static final long MIN_TIME = 1388534400000L; // 2014-01-01 00:00:00 GMT

	public static final int MIN_BUILD_VERSION = 64000;

	public static final Version MIN_VERSION = new Version(2, 2, 1);

	/**
	 *
	 */
	private FFSoftPolicy() {}

	/**
	 * @param ffInfo
	 * @return
	 */
	public static SoftFound toSoftFound(FFInfo ffInfo) {
		Objects.requireNonNull(ffInfo);

		Version version = ffInfo.getVersion();
		if(version != null) {
			return check(version.isUpperOrEqualsThan(MIN_VERSION), ffInfo);
		}

		Integer builtVersion = ffInfo.getBuiltVersion();
		if(builtVersion != null) {
			return check(builtVersion > MIN_BUILD_VERSION, ffInfo);
		}

		Date builtDate = ffInfo.getBuiltDate();
		if(builtDate != null) {
			return check(builtDate.getTime() > MIN_TIME, ffInfo);
		}

		return SoftFound.foundError(ffInfo.getFile(), "Unable to verify the soft: " + ffInfo);
	}

	// ******************************************************

	/**
	 * @param ffInfo
	 * @return
	 */
	private static SoftFound check(boolean verify, FFInfo ffInfo) {
		if(verify) {
			return SoftFound.found(ffInfo);
		}
		return SoftFound.foundBadVersion(ffInfo, "require at least " + MIN_VERSION + ", or built " + MIN_BUILD_VERSION
				+ ", or built date 2014-01-01");
	}

}
