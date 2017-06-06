package org.fagu.fmv.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.junit.Ignore;
import org.junit.Test;


public class TSTest {

	@Test
	@Ignore
	public void test() throws IOException {
		MovieMetadatas metadatas = MovieMetadatas.with(new File("d:\\tmp\\dvdout.ts")).extract();
		List<Stream> streams = metadatas.getStreams();
		System.out.println("count: " + streams.size());
		for(Stream stream : streams) {
			System.out.println(stream);
			if(stream.is(Type.VIDEO)) {
				VideoStream videoStream = (VideoStream)stream;
				System.out.println("   " + videoStream);
			} else if(stream.is(Type.AUDIO)) {
				AudioStream audioStream = (AudioStream)stream;
				System.out.println("   " + audioStream);
			}
		}
	}

}
