package org.fagu.fmv.ffmpeg.operation;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author f.agu
 */
public class ExtractThumbnail {

	private final List<File> files;

	public ExtractThumbnail(List<File> files) {
		this.files = files;
	}

	public List<File> getFiles() {
		return files;
	}

	public static ExtractThumbnail find(File folder, String spattern) {
		return find(folder, spattern, null);
	}

	public static ExtractThumbnail find(File folder, String spattern, Comparator<File> comparator) {
		final Pattern pattern = Pattern.compile(spattern);
		File[] files = folder.listFiles(pathname -> {
			if( ! pathname.isFile()) {
				return false;
			}
			return pattern.matcher(pathname.getName()).matches();
		});
		List<File> list = new ArrayList<>(Arrays.asList(files));
		Collections.sort(list, comparator);
		return new ExtractThumbnail(Arrays.asList(files));
	}

}
