package org.fagu.fmv.ffmpeg;

import java.io.File;

import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.Rotate;
import org.fagu.fmv.ffmpeg.ioe.PipeMediaInput;
import org.fagu.fmv.utils.media.Rotation;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 * @created 24 mai 2017 15:00:12
 */
public class StreamTestCase {

	@Test
	@Ignore
	public void testInputToFile() throws Exception {
		File outFile = new File("d:\\tmp\\out.mp4");
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.addMediaInput(new PipeMediaInput());
		builder.filter(Rotate.create(Rotation.R_90));
		builder.addMediaOutputFile(outFile)
				.overwrite();

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLine());
		executor.input(() -> ResourceUtils.open("mp4.mp4"));
		executor.execute();

	}
}
