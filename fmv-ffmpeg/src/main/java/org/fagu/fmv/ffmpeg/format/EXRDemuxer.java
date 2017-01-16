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

import org.fagu.fmv.ffmpeg.ioe.FileMediaInput;
import org.fagu.fmv.ffmpeg.operation.MediaInput;


/**
 * @author f.agu
 */
public class EXRDemuxer extends ImageDemuxer<EXRDemuxer> {

	/**
	 * @param mediaInput
	 */
	protected EXRDemuxer(MediaInput mediaInput) {
		super("exr", mediaInput);
	}

	/**
	 * @param file
	 * @return
	 */
	public static EXRDemuxer from(File file) {
		return new EXRDemuxer(new FileMediaInput(file));
	}

}
