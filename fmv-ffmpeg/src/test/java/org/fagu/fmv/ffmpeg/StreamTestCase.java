package org.fagu.fmv.ffmpeg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.fagu.fmv.ffmpeg.coder.Libx264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.Rotate;
import org.fagu.fmv.ffmpeg.flags.Movflags;
import org.fagu.fmv.ffmpeg.format.Formats;
import org.fagu.fmv.ffmpeg.format.MP4Muxer;
import org.fagu.fmv.ffmpeg.ioe.Pipe;
import org.fagu.fmv.ffmpeg.ioe.PipeMediaInput;
import org.fagu.fmv.ffmpeg.ioe.PipeMediaOutput;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.BitStreamFilter;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.utils.media.Rotation;
import org.junit.Test;


/**
 * @author f.agu
 * @created 24 mai 2017 15:00:12
 */
public class StreamTestCase {

	@Test
	// @Ignore
	public void testInputPipeToFile() throws Exception {
		File outFile = new File("c:\\tmp\\out.ts");
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.addMediaInput(new PipeMediaInput())
				.add("-analyzeduration", Integer.toString(Integer.MAX_VALUE))
				.add("-probesize", Integer.toString(Integer.MAX_VALUE))
				.pixelFormat(PixelFormat.YUV420P);
		builder.filter(Rotate.create(Rotation.R_90));
		builder.addMediaOutputFile(outFile)
				.bitStream(Type.VIDEO, BitStreamFilter.H264_MP4TOANNEXB)
				.overwrite();

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLine());
		executor.input(() -> ResourceUtils.open("mp4.mp4"));
		executor.execute();

	}

	@Test
	// @Ignore
	public void testInputPipeToFile2() throws Exception {
		File outFile = new File("c:\\tmp\\out.mp4");
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

		builder.addMediaInput(new PipeMediaInput())
				// .add("-analyzeduration", Integer.toString(Integer.MAX_VALUE))
				// .add("-probesize", Integer.toString(Integer.MAX_VALUE))

				.format("mp4");
		builder.filter(Rotate.create(Rotation.R_90));
		builder.mux(MP4Muxer.to(outFile).movflags(Movflags.FASTSTART))
				.codec(Libx264.build().mostCompatible())
				.pixelFormat(PixelFormat.YUV420P)
				.overwrite();

		// .bitStream(Type.VIDEO, BitStreamFilter.H264_MP4TOANNEXB)
		// .overwrite();

		FFExecutor<Object> executor = builder.build();
		System.out.println(executor.getCommandLine());
		executor.input(() -> ResourceUtils.open("mp4.mp4"));
		executor.execute();

	}

	@Test
	public void testOutputPipeFromFile() throws Exception {
		File inFile = ResourceUtils.extract("mp4.mp4");
		File outFile = new File("c:\\tmp\\mp4-3.mp4");
		try (OutputStream outputStream = new FileOutputStream(outFile)) {
			FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();

			builder.addMediaInputFile(inFile);
			builder.filter(Rotate.create(Rotation.R_90));
			builder.addMediaOutput(new PipeMediaOutput(Pipe.OUT))
					.format(Formats.NUT.getName())
					.overwrite();

			FFExecutor<Object> executor = builder.build();
			System.out.println(executor.getCommandLine());
			executor.output(() -> outputStream);
			executor.execute();
		} finally {
			inFile.delete();
		}
	}

}
