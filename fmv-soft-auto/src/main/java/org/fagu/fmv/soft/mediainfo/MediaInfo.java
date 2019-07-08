package org.fagu.fmv.soft.mediainfo;

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
import org.fagu.fmv.soft.SoftSearch;


/**
 * @author f.agu
 */
public class MediaInfo {

	private MediaInfo() {
		throw new AssertionError("No instances for you!");
	}

	public static Soft search() {
		return Soft.search(new MediaInfoSoftProvider());
	}

	public static SoftSearch searchWith() {
		return Soft.with(MediaInfoSoftProvider::new);
	}

}
