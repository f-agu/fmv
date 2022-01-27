package org.fagu.fmv.ffmpeg.format;

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

import org.apache.commons.exec.OS;
import org.fagu.fmv.ffmpeg.ioe.FileMediaOutput;
import org.fagu.fmv.ffmpeg.operation.MediaOutput;


/**
 * @author f.agu
 */
public class NullMuxer extends Muxer<NullMuxer> {

	protected NullMuxer(MediaOutput mediaOutput) {
		super("null", mediaOutput);
	}

	public static NullMuxer build() {
		File file = null;
		if(OS.isFamilyWindows()) {
			file = new File("NUL");
		} else if(OS.isFamilyMac() || OS.isFamilyUnix()) {
			file = new File("/dev/null");
		}
		return new NullMuxer(new FileMediaOutput(file));
	}
}
