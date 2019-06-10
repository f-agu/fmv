package org.fagu.fmv.ffmpeg;

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Oodrive
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
