package org.fagu.fmv.ffmpeg.metadatas;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.ffmpeg.ResourceUtils;
import org.fagu.fmv.ffmpeg.utils.ChannelLayout;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 25 janv. 2017 11:52:24
 */
public class MovieMetadatasTestCase {

	/**
	 * 
	 */
	public MovieMetadatasTestCase() {}

	/**
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		File file = null;
		try {
			file = ResourceUtils.extract("mp4.mp4");
			MovieMetadatas movieMetadatas = MovieMetadatas.with(file).extract();

			// video
			VideoStream videoStream = movieMetadatas.getVideoStream();
			assertEquals(533148, videoStream.bitRate().getAsInt());
			assertEquals("h264", videoStream.codecName());
			assertEquals("video", videoStream.codecType());
			assertEquals(Duration.valueOf(1.335000F), videoStream.duration());
			assertEquals(40, videoStream.countEstimateFrames().getAsInt());

			// audio
			AudioStream audioStream = movieMetadatas.getAudioStream();
			assertEquals(128920, audioStream.bitRate().getAsInt());
			assertEquals(ChannelLayout.MONO, audioStream.channelLayout());
			assertEquals("aac", audioStream.codecName());
			assertEquals(Duration.parse("00:00:01.335"), videoStream.duration());

			// System.out.println(movieMetadatas);
		} finally {
			if(file != null) {
				file.delete();
			}
		}

	}

}
