package org.fagu.fmv.ffmpeg.operation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.fagu.fmv.utils.time.Time;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class FFMPEGProgressReadLineTestCase {

	@Test
	void test46() {
		FFMPEGProgressReadLine ffmpegProgressReadLine = new FFMPEGProgressReadLine();
		ffmpegProgressReadLine.read("frame=   46 fps=0.0 q=0.0 size=       0kB time=00:00:01.56 bitrate=   0.2kbits/s dup=1 drop=0");
		assertEquals(46, ffmpegProgressReadLine.getFrame());
		assertEquals(0, ffmpegProgressReadLine.getFps());
		assertEquals(0, ffmpegProgressReadLine.getQ());
		assertEquals(0, ffmpegProgressReadLine.getSizeKb());
		assertEquals(Time.valueOf(1.56), ffmpegProgressReadLine.getTime());
		assertEquals(0.2, ffmpegProgressReadLine.getBitRateKb(), 0.01);
		assertEquals(Integer.valueOf(1), ffmpegProgressReadLine.getDup());
		assertEquals(Integer.valueOf(0), ffmpegProgressReadLine.getDrop());
	}

	@Test
	void test52() {
		FFMPEGProgressReadLine ffmpegProgressReadLine = new FFMPEGProgressReadLine();
		ffmpegProgressReadLine.read("frame=   52 fps= 38 q=29.0 size=      64kB time=00:00:01.75 bitrate= 297.4kbits/s dup=1 drop=0 ");
		assertEquals(52, ffmpegProgressReadLine.getFrame());
		assertEquals(38, ffmpegProgressReadLine.getFps());
		assertEquals(29, ffmpegProgressReadLine.getQ());
		assertEquals(64, ffmpegProgressReadLine.getSizeKb());
		assertEquals(Time.valueOf(1.75), ffmpegProgressReadLine.getTime());
		assertEquals(297.4, ffmpegProgressReadLine.getBitRateKb(), 0.01);
		assertEquals(Integer.valueOf(1), ffmpegProgressReadLine.getDup());
		assertEquals(Integer.valueOf(0), ffmpegProgressReadLine.getDrop());
	}

	@Test
	void test53() {
		FFMPEGProgressReadLine ffmpegProgressReadLine = new FFMPEGProgressReadLine();
		ffmpegProgressReadLine.read("frame=   52 fps= 38 q=29.0 size=      64kB time=00:00:01.75 bitrate= 297.4kbits/s speed=0.731x ");
		assertEquals(52, ffmpegProgressReadLine.getFrame());
		assertEquals(38, ffmpegProgressReadLine.getFps());
		assertEquals(29, ffmpegProgressReadLine.getQ());
		assertEquals(64, ffmpegProgressReadLine.getSizeKb());
		assertEquals(Time.valueOf(1.75), ffmpegProgressReadLine.getTime());
		assertEquals(297.4, ffmpegProgressReadLine.getBitRateKb(), 0.01);
		assertEquals(0.731, ffmpegProgressReadLine.getSpeed(), 0.0001D);
	}

	@Test
	void test54() {
		FFMPEGProgressReadLine ffmpegProgressReadLine = new FFMPEGProgressReadLine();
		ffmpegProgressReadLine.read("frame=83009 fps= 17 q=28.0 size=  315987kB time=00:57:42.38 bitrate= 747.6kbits/s speed=0.73x");
		assertEquals(83009, ffmpegProgressReadLine.getFrame());
		assertEquals(17, ffmpegProgressReadLine.getFps());
		assertEquals(28, ffmpegProgressReadLine.getQ());
		assertEquals(315987, ffmpegProgressReadLine.getSizeKb());
		assertEquals(Time.valueOf(57D * 60D + 42.38), ffmpegProgressReadLine.getTime());
		assertEquals(747.6, ffmpegProgressReadLine.getBitRateKb(), 0.01);
		assertEquals(0.73, ffmpegProgressReadLine.getSpeed(), 0.0001D);
	}

	//

	@Test
	void testAudio() {
		FFMPEGProgressReadLine ffmpegProgressReadLine = new FFMPEGProgressReadLine();
		ffmpegProgressReadLine.read("size=    1655kB time=00:01:45.79 bitrate= 128.1kbits/s ");
		assertEquals(1655, ffmpegProgressReadLine.getSizeKb());
		assertEquals(Time.valueOf(105.79), ffmpegProgressReadLine.getTime());
		assertEquals(128.1, ffmpegProgressReadLine.getBitRateKb(), 0.01);
	}

	@Test
	void testOtherLines() throws Exception {
		FFMPEGProgressReadLine ffmpegProgressReadLine = new FFMPEGProgressReadLine();
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("ffmpeg-output1")))) {
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				ffmpegProgressReadLine.read(line);
			}
		}
		assertEquals(0, ffmpegProgressReadLine.getFrame());
		assertEquals(0, ffmpegProgressReadLine.getFps());
		assertNull(ffmpegProgressReadLine.getTime());
	}

}
