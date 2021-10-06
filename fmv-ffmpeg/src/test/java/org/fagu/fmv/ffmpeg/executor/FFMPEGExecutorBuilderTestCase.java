package org.fagu.fmv.ffmpeg.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

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

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.fagu.fmv.ffmpeg.operation.FFMPEGOperation;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.MediaOutput;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.utils.media.Size;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * @author f.agu
 */
@ExtendWith(MockitoExtension.class)
class FFMPEGExecutorBuilderTestCase {

	private FFMPEGExecutorBuilder ffmpegExecutorBuilder;

	@BeforeEach
	void setUp() {
		ffmpegExecutorBuilder = FFMPEGExecutorBuilder.create();
	}

	// ======================== INPUT ========================

	@Test
	void testInputFile() {
		File file = mockFile("/path/file");
		ffmpegExecutorBuilder.addMediaInputFile(file);
		assertArgs("-i", "/path/file");
	}

	@Test
	void testInput() {
		MediaInput input = mockInput("/path/file");
		ffmpegExecutorBuilder.addMediaInput(input);
		assertArgs("-i", "/path/file");
	}

	// ======================== INPUT PROCESSOR ========================

	@Test
	void testInputProcessor_duration() {
		MediaInput input = mockInput("/path/file");
		InputProcessor inputProcessor = ffmpegExecutorBuilder.addMediaInput(input);
		inputProcessor.duration(new Duration(1, 2, 3.4));
		assertArgs("-t", "01:02:03.400", "-i", "/path/file");
	}

	@Test
	void testInputProcessor_format() {
		MediaInput input = mockInput("/path/file");
		InputProcessor inputProcessor = ffmpegExecutorBuilder.addMediaInput(input);
		inputProcessor.format("forMAT");
		assertArgs("-f", "forMAT", "-i", "/path/file");
	}

	@Test
	void testInputProcessor_frameRate() {
		MediaInput input = mockInput("/path/file");
		InputProcessor inputProcessor = ffmpegExecutorBuilder.addMediaInput(input);
		inputProcessor.frameRate(FrameRate.PAL);
		assertArgs("-r", "25", "-i", "/path/file");
	}

	@Test
	void testInputProcessor_getInput() {
		MediaInput input = mock(MediaInput.class);
		InputProcessor inputProcessor = ffmpegExecutorBuilder.addMediaInput(input);
		assertSame(input, inputProcessor.getInput());
	}

	@Test
	void testInputProcessor_size() {
		MediaInput input = mockInput("/path/file");
		InputProcessor inputProcessor = ffmpegExecutorBuilder.addMediaInput(input);
		inputProcessor.size(Size.HD720);
		assertArgs("-s", "hd720", "-i", "/path/file");
	}

	@Test
	void testInputProcessor_timeSeek() {
		MediaInput input = mockInput("/path/file");
		InputProcessor inputProcessor = ffmpegExecutorBuilder.addMediaInput(input);
		inputProcessor.timeSeek(new Time(1, 2, 3.4));
		assertArgs("-ss", "01:02:03.400", "-i", "/path/file");
	}

	// ======================== OUTPUT ========================

	@Test
	void testOutputFile() {
		File file = mockFile("/path/file");
		ffmpegExecutorBuilder.addMediaOutputFile(file);
		assertArgs("/path/file");
	}

	@Test
	void testOutput() {
		MediaOutput output = mockOutput("/path/file");
		ffmpegExecutorBuilder.addMediaOutput(output);
		assertArgs("/path/file");
	}

	// ======================== OUTPUT PROCESSOR ========================

	@Test
	void testOutputProcessor_duration() {
		MediaOutput output = mockOutput("/path/file");
		OutputProcessor outputProcessor = ffmpegExecutorBuilder.addMediaOutput(output);
		outputProcessor.duration(new Duration(1, 2, 3.4));
		assertArgs("-t", "01:02:03.400", "/path/file");
	}

	@Test
	void testOutputProcessor_format() {
		MediaOutput output = mockOutput("/path/file");
		OutputProcessor outputProcessor = ffmpegExecutorBuilder.addMediaOutput(output);
		outputProcessor.format("forMAT");
		assertArgs("-f", "forMAT", "/path/file");
	}

	// ***********************************************************

	private void assertArgs(String... strs) {
		FFMPEGOperation<?> ffmpegOperation = ffmpegExecutorBuilder.getFFMPEGOperation();
		List<String> arguments = ffmpegOperation.toArguments();
		assertEquals(strs.length, arguments.size());
		Iterator<String> iterator = arguments.iterator();
		int pos = 0;
		for(String str : strs) {
			assertEquals(str, iterator.next(), "arg n" + Integer.toString(pos++));
		}
	}

	private MediaInput mockInput(String location) {
		MediaInput input = mock(MediaInput.class);
		doReturn(location).when(input).toString();
		return input;
	}

	private MediaOutput mockOutput(String location) {
		MediaOutput output = mock(MediaOutput.class);
		doReturn(location).when(output).toString();
		return output;
	}

	private File mockFile(String path) {
		File file = mock(File.class);
		doReturn(path).when(file).getPath();
		return file;
	}
}
