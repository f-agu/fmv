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
public enum PatternType {
	// select glob/sequence pattern type
	GLOB_SEQUENCE("glob_sequence"),
	// select glob pattern type
	GLOB("glob"),
	// select sequence pattern type
	SEQUENCE("sequence");

	private String flag;

	/**
	 * @param flag
	 */
	private PatternType(String flag) {
		this.flag = flag;
	}

	/**
	 * @return
	 */
	public String flag() {
		return flag;
	}

}
