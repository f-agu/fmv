package org.fagu.fmv.ffmpeg.operation;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2015 fagu
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


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;


/**
 * @author f.agu
 */
public class LibLogReadLineTestCase {

	/**
	 *
	 */
	public LibLogReadLineTestCase() {}

	/**
	 *
	 */
	@Test
	public void testEmpty() {
		LibLogReadLine llrl = new LibLogReadLine();
		llrl.read("[libx264 @ 03c64160] profile High, level 3.0");
	}

	/**
	 *
	 */
	@Test
	public void testSimplelibx264() {
		LibLog libLog = mock(LibLog.class);
		LibLogReadLine llrl = new LibLogReadLine();
		llrl.add(s -> true, libLog);

		llrl.read("[libx264 @ 03c64160] profile High, level 3.0");

		verify(libLog).log("libx264", "03c64160", "profile High, level 3.0");
	}

	/**
	 *
	 */
	@Test
	public void testSimplelibx264_2_Filters_for_1_LibLog() {
		LibLog libLog = mock(LibLog.class);
		LibLogReadLine llrl = new LibLogReadLine();
		llrl.add(s -> s.startsWith("libx"), libLog);
		llrl.add(s -> s.endsWith("264"), libLog);

		llrl.read("[libx264 @ 03c64160] profile High, level 3.0");

		verify(libLog).log("libx264", "03c64160", "profile High, level 3.0");
	}

	/**
	 *
	 */
	@Test
	public void testSimpleVolumedetect() {
		LibLog libLog = mock(LibLog.class);
		LibLogReadLine llrl = new LibLogReadLine();
		llrl.add(s -> s.startsWith("Parsed_volumedetect"), libLog);

		llrl.read("[Parsed_volumedetect_0 @ 0394e8e0] n_samples: 191488");
		verify(libLog).log("Parsed_volumedetect_0", "0394e8e0", "n_samples: 191488");

		llrl.read("[Parsed_volumedetect_0 @ 0394e8e0] mean_volume: -17.9 dB");
		verify(libLog).log("Parsed_volumedetect_0", "0394e8e0", "mean_volume: -17.9 dB");

		llrl.read("[Parsed_volumedetect_0 @ 0394e8e0] max_volume: 0.0 dB");
		verify(libLog).log("Parsed_volumedetect_0", "0394e8e0", "max_volume: 0.0 dB");

		llrl.read("[Parsed_volumedetect_0 @ 0394e8e0] histogram_0db: 46");
		verify(libLog).log("Parsed_volumedetect_0", "0394e8e0", "histogram_0db: 46");
	}

}
