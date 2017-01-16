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
public class Image2Muxer extends Muxer<Image2Muxer> {

	/**
	 * @param mediaOutput
	 */
	protected Image2Muxer(MediaOutput mediaOutput) {
		super("image2", mediaOutput);
	}

	/**
	 * @param file
	 * @return
	 */
	public static Image2Muxer to(File file) {
		return new Image2Muxer(new FileMediaOutput(file));
	}

	/**
	 * Continuously overwrite one file (from 0 to 1) (default 0)
	 * 
	 * @param updatefirst
	 * @return
	 */
	public Image2Muxer updatefirst(boolean updatefirst) {
		parameter("-updatefirst", Integer.toString(updatefirst ? 1 : 0));
		return getMThis();
	}

	/**
	 * Continuously overwrite one file (from 0 to 1) (default 0)
	 * 
	 * @param update
	 * @return
	 */
	public Image2Muxer update(boolean update) {
		parameter("-update", Integer.toString(update ? 1 : 0));
		return getMThis();
	}

	/**
	 * Set first number in the sequence (from 0 to INT_MAX) (default 1)
	 * 
	 * @param startNumber
	 * @return
	 */
	public Image2Muxer startNumber(int startNumber) {
		if(startNumber < 0) {
			throw new IllegalArgumentException("startNumber must be at least 0");
		}
		parameter("-start_number", Integer.toString(startNumber));
		return getMThis();
	}

	/**
	 * Use strftime for filename (from 0 to 1) (default 0)
	 * 
	 * @param strftime
	 * @return
	 */
	public Image2Muxer strftime(boolean strftime) {
		parameter("-strftime", Integer.toString(strftime ? 1 : 0));
		return getMThis();
	}

}
