package org.fagu.fmv.ffmpeg.operation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.io.File;

import org.fagu.fmv.ffmpeg.ioe.FileMediaOutput;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * @author f.agu
 */
class MetadataVersionTestCase {

	@Test
	void test() {
		OutputProcessor outputProcessor = mock(OutputProcessor.class);
		FileMediaOutput fileMediaOutput = mock(FileMediaOutput.class);

		doReturn(fileMediaOutput).when(outputProcessor).getMediaOutput();
		doReturn(new File("x.mp4")).when(fileMediaOutput).getFile();

		MetadataVersion.add(outputProcessor);

		verify(outputProcessor).metadataStream(eq(Type.VIDEO), eq("comment"), anyString());
	}

}
