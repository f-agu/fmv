/**
 * 
 */
package org.fagu.fmv.ffmpeg;

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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Duration;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class MockMovieMetadatas {

	private int countStreams;

	private List<Stream> streams = new ArrayList<>();

	private List<AudioStream> audioStreams = new ArrayList<>();

	private List<VideoStream> videoStreams = new ArrayList<>();

	private MovieMetadatas movieMetadatas;

	/**
	 *
	 */
	private MockMovieMetadatas() {
		movieMetadatas = mock(MovieMetadatas.class);
		doReturn(audioStreams).when(movieMetadatas).getAudioStreams();
		doReturn(videoStreams).when(movieMetadatas).getVideoStreams();
		doReturn(streams).when(movieMetadatas).getStreams();
	}

	/**
	 * @return
	 */
	public static MockMovieMetadatas builder() {
		return new MockMovieMetadatas();
	}

	/**
	 * @return
	 */
	public MockAudioStream audio() {
		AudioStream audioStream = mock(AudioStream.class);
		doReturn(audioStream).when(movieMetadatas).getAudioStream();
		addStream(audioStream, Type.AUDIO, audioStreams);
		return new MockAudioStream(audioStream);
	}

	/**
	 * @return
	 */
	public MockVideoStream video() {
		VideoStream videoStream = mock(VideoStream.class);
		doReturn(videoStream).when(movieMetadatas).getVideoStream();
		addStream(videoStream, Type.VIDEO, videoStreams);
		return new MockVideoStream(videoStream);
	}

	/**
	 * @return
	 */
	public MovieMetadatas build() {
		return movieMetadatas;
	}

	// *******************************

	/**
	 * @param stream
	 * @param type
	 * @param streams
	 */
	private <S extends Stream> void addStream(S stream, Type type, List<S> streams) {
		doReturn(true).when(movieMetadatas).contains(type);
		doReturn(type).when(stream).type();
		doReturn(countStreams++).when(stream).index();
		streams.add(stream);
		this.streams.add(stream);
	}

	// --------------------------------------------------------

	/**
	 * @author f.agu
	 */
	public static class MockVideoStream {

		private VideoStream videoStream;

		private MockVideoStream(VideoStream videoStream) {
			this.videoStream = videoStream;
		}

		public MockVideoStream rotation(Rotation rotation) {
			doReturn(rotation).when(videoStream).rotate();
			return this;
		}

		public MockVideoStream size(Size size) {
			doReturn(size).when(videoStream).size();
			return this;
		}

		public MockVideoStream duration(Duration duration) {
			doReturn(duration).when(videoStream).duration();
			return this;
		}

		public MockVideoStream countEstimateFrames(Integer count) {
			doReturn(count).when(videoStream).countEstimateFrames();
			return this;
		}

	}

	// --------------------------------------------------------
	/**
	 * @author f.agu
	 */
	public static class MockAudioStream {

		private AudioStream audioStream;

		private MockAudioStream(AudioStream audioStream) {
			this.audioStream = audioStream;
		}

		public MockAudioStream sampleRate(Integer rate) {
			doReturn(rate).when(audioStream).sampleRate();
			return this;
		}

	}

}
