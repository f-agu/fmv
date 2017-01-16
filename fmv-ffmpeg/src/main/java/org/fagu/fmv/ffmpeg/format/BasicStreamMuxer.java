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

import org.fagu.fmv.ffmpeg.ioe.FileMediaOutput;
import org.fagu.fmv.ffmpeg.operation.MediaOutput;


/**
 * @author f.agu
 */
public class BasicStreamMuxer extends StreamMuxer<BasicStreamMuxer> {

	/**
	 * @param mediaOutput
	 */
	protected BasicStreamMuxer(MediaOutput mediaOutput) {
		super("", mediaOutput);
	}

	/**
	 * @param format
	 * @param mediaOutput
	 */
	protected BasicStreamMuxer(String format, MediaOutput mediaOutput) {
		super(format, mediaOutput);
	}

	/**
	 * @param file
	 * @return
	 */
	public static BasicStreamMuxer to(File file) {
		return new BasicStreamMuxer(new FileMediaOutput(file));
	}

	/**
	 * @param file
	 * @param format
	 * @return
	 */
	public static BasicStreamMuxer to(File file, String format) {
		return new BasicStreamMuxer(format, new FileMediaOutput(file));
	}

}
