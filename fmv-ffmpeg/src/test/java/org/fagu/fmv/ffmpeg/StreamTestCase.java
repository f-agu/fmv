package org.fagu.fmv.ffmpeg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.fagu.fmv.ffmpeg.coder.Libx264;
import org.fagu.fmv.ffmpeg.executor.Executed;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.Rotate;
import org.fagu.fmv.ffmpeg.flags.Movflags;
import org.fagu.fmv.ffmpeg.format.Formats;
import org.fagu.fmv.ffmpeg.format.MP4Muxer;
import org.fagu.fmv.ffmpeg.ioe.Pipe;
import org.fagu.fmv.ffmpeg.ioe.PipeMediaInput;
import org.fagu.fmv.ffmpeg.ioe.PipeMediaOutput;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.operation.InfoOperation;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.BitStreamFilter;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;
import org.fagu.fmv.soft.io.StreamLog;
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
		System.out.println(executor.getCommandLineString());
		executor.input(() -> ResourceUtils.open("mp4.mp4"));
		executor.execute();

	}

	@Test
	@Ignore
	public void testInputPipeForInfo() throws Exception {
		StreamLog.debug(true);
		extractInfoWithInputPipe("3gp.3gp");
		extractInfoWithInputPipe("avi.avi");
		extractInfoWithInputPipe("flv.flv");
		extractInfoWithInputPipe("melt.mpg");
		extractInfoWithInputPipe("mp4.mp4");
		extractInfoWithInputPipe("mpeg.mpeg");
	}

	private void extractInfoWithInputPipe(String resource) throws Exception {
		InfoOperation infoOperation = new InfoOperation(new PipeMediaInput());
		FFExecutor<MovieMetadatas> executor = new FFExecutor<>(infoOperation);
		System.out.println(executor.getCommandLineString());
		executor.input(() -> ResourceUtils.open(resource));
		Executed<MovieMetadatas> execute = executor.execute();
		// System.out.println(execute.getResult());
	}

	@Test
	@Ignore
	public void testInputPipeToFile2() throws Exception {
		File outFile = new File("C:\\Oodrive\\video\\mp4-2\\video_320x180_500k-piped.mp4");
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		// new PipeMediaInput().parameter("-movflags", "faststart");
		builder.addMediaInput(new PipeMediaInput()).add("-movflags", "faststart")
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
		System.out.println(executor.getCommandLineString());
		executor.input(() -> new FileInputStream("C:\\Oodrive\\video\\mp4-2\\video_320x180_500k.mp4"));
		executor.execute();

	}

	@Test
	@Ignore
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

			// Formats.MP4 : [mp4 @ 0000022fd170ddc0] muxer does not support non seekable output

			FFExecutor<Object> executor = builder.build();
			System.out.println(executor.getCommandLineString());
			executor.output(() -> outputStream);
			executor.execute();
		} finally {
			inFile.delete();
		}
	}

}
