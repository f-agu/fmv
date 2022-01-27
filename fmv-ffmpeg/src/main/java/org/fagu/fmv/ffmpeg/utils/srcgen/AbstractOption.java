package org.fagu.fmv.ffmpeg.utils.srcgen;

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
public abstract class AbstractOption {

	private final String name;

	private final Flags flags;

	private final String description;

	public AbstractOption(String name, Flags flags, String description) {
		this.name = name;
		this.flags = flags;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public Flags getFlags() {
		return flags;
	}

	public String getDescription() {
		return description;
	}

}
