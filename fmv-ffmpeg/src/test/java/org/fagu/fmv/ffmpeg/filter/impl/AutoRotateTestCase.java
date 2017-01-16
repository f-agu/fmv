package org.fagu.fmv.ffmpeg.filter.impl;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2016 fagu
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


import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 nov. 2016 14:36:03
 */
public class AutoRotateTestCase {

	/**
	 * 
	 */
	public AutoRotateTestCase() {}

	@Test
	public void test() {
		MovieMetadatas movieMetadatas = mock(MovieMetadatas.class);
		VideoStream videoStream = mock(VideoStream.class);

		doReturn(videoStream).when(movieMetadatas).getVideoStream();
		doReturn("90").when(videoStream).tag("rotate");

		AutoRotate.create(movieMetadatas);
	}

}
