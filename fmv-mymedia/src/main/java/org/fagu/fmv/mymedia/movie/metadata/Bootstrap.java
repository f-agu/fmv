package org.fagu.fmv.mymedia.movie.metadata;

import java.io.File;
import java.io.IOException;

import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;


/**
 * @author Oodrive
 * @author f.agu
 * @created 18 oct. 2022 08:49:55
 */
public class Bootstrap {

	public static void main(String... args) throws IOException {
		File file = new File("c:\\tmp\\transform\\20220821_115707.mp4");
		MovieMetadatas movieMetadatas = MovieMetadatas.with(file).extract();
		System.out.println(movieMetadatas.toJSON());
		System.out.println(movieMetadatas.getFormat().creationDate());
		System.out.println(movieMetadatas.getFormat().createDateTime());
	}

}
