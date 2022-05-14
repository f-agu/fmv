package org.fagu.fmv.ffmpeg.ioe;

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
import java.util.Objects;

import org.fagu.fmv.ffmpeg.operation.MediaOutput;


/**
 * @author f.agu
 */
public class FileMediaOutput extends AbstractIOEntity<FileMediaOutput> implements MediaOutput {

	private final File file;

	public FileMediaOutput(File file) {
		super("file");
		this.file = Objects.requireNonNull(file);
	}

	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		return file.getPath();
	}
}
