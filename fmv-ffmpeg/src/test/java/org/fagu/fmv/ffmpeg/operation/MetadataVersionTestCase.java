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


import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.fagu.fmv.ffmpeg.ioe.FileMediaOutput;
import org.junit.Test;


/**
 * @author f.agu
 */
public class MetadataVersionTestCase {

	/**
	 *
	 */
	public MetadataVersionTestCase() {}

	/**
	 *
	 */
	@Test
	public void test() {
		OutputProcessor outputProcessor = mock(OutputProcessor.class);
		FileMediaOutput fileMediaOutput = mock(FileMediaOutput.class);

		doReturn(fileMediaOutput).when(outputProcessor).getMediaOutput();
		doReturn(new File("x.mp4")).when(fileMediaOutput).getFile();

		MetadataVersion.add(outputProcessor);

		verify(outputProcessor).metadataStream(eq(Type.VIDEO), eq("comment"), anyString());
	}

}
