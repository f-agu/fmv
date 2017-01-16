package org.fagu.fmv.mymedia.utils;

/*
 * #%L
 * fmv-mymedia
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

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.fagu.fmv.ffmpeg.operation.Progress;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class FFMpegTextProgressBarTestCase {

	/**
	 *
	 */
	public FFMpegTextProgressBarTestCase() {}

	/**
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void test0() throws Exception {
		final int MAX_FRAMES = 270000;
		final int STEP_FRAMES = 10000;
		Progress progress = mock(Progress.class);
		AtomicInteger frame = new AtomicInteger();
		AtomicInteger sizekb = new AtomicInteger();

		doAnswer(invocation -> {
			sizekb.addAndGet(1);
			return Math.min(frame.addAndGet(STEP_FRAMES), MAX_FRAMES);
		}).when(progress).getFrame();

		doAnswer(invocation -> sizekb.get()).when(progress).getSizeKb();

		FFMpegTextProgressBar ffMpegTextProgressBar = new FFMpegTextProgressBar();
		try {
			ffMpegTextProgressBar.prepareProgressBar(progress, "consolePrefixMessage", ffMpegTextProgressBar.progressByFrame(MAX_FRAMES, 50L * 1024L));
			while(MAX_FRAMES > frame.get()) {
				Thread.sleep(200);
			}
		} finally {
			ffMpegTextProgressBar.close(50);
		}
	}

}
