package org.fagu.fmv.soft.find;

import java.io.File;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;


/*
 * #%L
 * fmv-utils
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
public abstract class SoftInfo implements Comparable<SoftInfo> {

	private final File file;

	private final String softName;

	public SoftInfo(File file, String softName) {
		this.file = Objects.requireNonNull(file);
		this.softName = Objects.requireNonNull(softName);
	}

	// ***************************************

	public abstract String getInfo();

	// ***************************************

	public String getName() {
		return softName;
	}

	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(80)
				.append(getName()).append('[');
		File f = getFile();
		if(f != null) {
			buf.append(f.getAbsolutePath());
			if( ! f.exists()) {
				buf.append(" (not exists)");
			}
		} else {
			buf.append("File undefined");
		}
		String info = getInfo();
		if(StringUtils.isNotBlank(info)) {
			buf.append(", ").append(info);
		}
		return buf.append(']')
				.toString();
	}
}
