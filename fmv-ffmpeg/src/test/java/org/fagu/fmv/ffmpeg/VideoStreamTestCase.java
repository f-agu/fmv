package org.fagu.fmv.ffmpeg;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.io.IOException;

import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 * @created 10 juin 2019 11:07:22
 */
@Ignore
public class VideoStreamTestCase {

	@Test
	public void testDuration() throws IOException {
		File file = null;
		try {
			file = ResourceUtils.extract("melt.mpg");
			MovieMetadatas movieMetadatas = MovieMetadatas.with(file).extract();
			movieMetadatas.getVideoStream().countEstimateFrames();
		} finally {
			if(file != null) {
				file.delete();
			}
		}
	}

}
