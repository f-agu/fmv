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

import org.fagu.fmv.ffmpeg.ioe.PipeMediaInput;
import org.fagu.fmv.ffmpeg.operation.MediaInput;


/**
 * @author f.agu
 */
public class Image2PipeDemuxer extends ImageDemuxer<Image2PipeDemuxer> {

	protected Image2PipeDemuxer(MediaInput mediaInput) {
		super("image2pipe", mediaInput);
	}

	public static Image2PipeDemuxer build() {
		return new Image2PipeDemuxer(new PipeMediaInput());
	}

}
