package org.fagu.fmv.ffmpeg.flags;

/*
 * #%L
 * fmv-ffmpeg
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

/**
 * @author f.agu
 */
public enum TimestampFromFile {
	// none
	NONE("none"),
	// second precision
	SEC("sec"),
	// nano second precision
	NS("ns");

	private final String flag;

	/**
	 * @param flag
	 */
	private TimestampFromFile(String flag) {
		this.flag = flag;
	}

	/**
	 * @return
	 */
	public String flag() {
		return flag;
	}

}
