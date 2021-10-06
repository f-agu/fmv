package org.fagu.fmv.ffmpeg.metadatas;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2017 fagu
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.fagu.fmv.ffmpeg.ResourceUtils;
import org.fagu.fmv.ffmpeg.utils.ChannelLayout;
import org.fagu.fmv.ffmpeg.utils.Fraction;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.utils.media.Ratio;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.fmv.utils.media.Size;
import org.fagu.fmv.utils.time.Duration;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 * @created 25 janv. 2017 11:52:24
 */
class MovieMetadatasTestCase {

	@Test
	void testParse() throws IOException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("VID_20190624_113754.json"),
				StandardCharsets.UTF_8))) {
			reader.lines().forEach(sb::append);
		}
		MovieMetadatas movieMetadatas = MovieMetadatas.parseJSON(sb.toString());

		Format format = movieMetadatas.getFormat();
		assertEquals(Duration.valueOf(4.368D), format.duration().get());
		assertEquals(9686666, format.size().get().intValue());
		assertEquals(17741146, format.bitRate().get().intValue());
		assertEquals("48° 52' 21.72\" N, 2° 20' 55.68\" E", format.coordinates().get().toString());

		VideoStream videoStream = movieMetadatas.getVideoStream();
		assertEquals(0, videoStream.index());
		assertEquals(Size.HD1080, videoStream.size());
		assertEquals(new Fraction(1, 90_000), videoStream.timeBase().get());
		assertEquals(Duration.valueOf(4.367511D), videoStream.duration().get());
		assertEquals(Integer.valueOf(16908848), videoStream.bitRate().get());
		assertEquals("video", videoStream.codecType().get());
		assertEquals("avc1", videoStream.codecTagString());
		assertEquals("0x31637661", videoStream.codecTag().get());
		assertEquals(new Fraction(98269, 5895000), videoStream.codecTimeBase().get());
		assertEquals(Ratio._16_9, videoStream.displayAspectRatio().get());
		assertEquals(Ratio.ONE, videoStream.sampleAspectRatio().get());
		assertEquals(PixelFormat.YUVJ420P, videoStream.pixelFormat().get());
		assertEquals(Rotation.R_270, videoStream.rotation());
		assertEquals("eng", videoStream.language().get());

		AudioStream audioStream = movieMetadatas.getAudioStream();
		assertEquals(1, audioStream.index());
		assertEquals("aac", audioStream.codecName().get());
		assertEquals("audio", audioStream.codecType().get());
		assertEquals(new Fraction(1, 48000), audioStream.codecTimeBase().get());
		assertEquals("mp4a", audioStream.codecTagString());
		assertEquals("0x6134706d", audioStream.codecTag().get());
	}

	@Test
	void testExtractOnFile() throws IOException {
		File file = null;
		try {
			file = ResourceUtils.extract("mp4.mp4");
			MovieMetadatas movieMetadatas = MovieMetadatas.with(file).extract();
			assertMetadataResources(movieMetadatas);
		} finally {
			if(file != null) {
				file.delete();
			}
		}
	}

	@Test
	void testExtractOnInputStream() throws IOException {
		try (InputStream inputStream = ResourceUtils.open("mp4.mp4")) {
			MovieMetadatas movieMetadatas = MovieMetadatas.with(inputStream)
					.extract();
			assertMetadataResources(movieMetadatas);
		}
	}

	// ****************************************************************

	private void assertMetadataResources(MovieMetadatas movieMetadatas) {
		// video
		VideoStream videoStream = movieMetadatas.getVideoStream();
		assertEquals(533148, videoStream.bitRate().get().intValue());
		assertEquals("h264", videoStream.codecName().get());
		assertEquals("video", videoStream.codecType().get());
		assertEquals(Duration.valueOf(1.335000F), videoStream.duration().get());
		assertEquals(40, videoStream.countEstimateFrames().get().intValue());

		// audio
		AudioStream audioStream = movieMetadatas.getAudioStream();
		assertEquals(128920, audioStream.bitRate().get().intValue());
		assertEquals(ChannelLayout.MONO, audioStream.channelLayout().get());
		assertEquals("aac", audioStream.codecName().get());
		assertEquals(Duration.parse("00:00:01.335"), videoStream.duration().get());
	}

}
