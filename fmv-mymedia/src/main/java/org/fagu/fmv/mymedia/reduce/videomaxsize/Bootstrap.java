package org.fagu.fmv.mymedia.reduce.videomaxsize;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.coder.H264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.AutoRotate;
import org.fagu.fmv.ffmpeg.filter.impl.Scale;
import org.fagu.fmv.ffmpeg.filter.impl.ScaleMode;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.format.Formats;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.ffmpeg.progressbar.FFMpegProgressBar;
import org.fagu.fmv.media.FileType;
import org.fagu.fmv.media.FileTypeUtils;
import org.fagu.fmv.media.FileTypeUtils.FileIs;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.utils.media.Size;


/**
 * @author Utilisateur
 * @created 11 févr. 2022 14:13:58
 */
public class Bootstrap {

	private static final int CRF = 23;

	private static final Size MAX_SIZE = Size.HD1080;

	public static void main(String... args) {
		browse(new File("..."));
	}

	private static void browse(File folder) {
		FileIs fileIs = FileTypeUtils.with(FileType.VIDEO);
		File[] files = folder.listFiles(fileIs::verify);
		if(files != null) {
			for(File file : files) {
				try {
					MovieMetadatas metadatas = MovieMetadatas.with(file).extract();
					if(needToResize(metadatas, MAX_SIZE)) {
						File destFile = getTempFile(file);
						resizeVideo(metadatas, file, destFile, MAX_SIZE, file.getName());
						file.delete();
						destFile.renameTo(file);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static File getTempFile(File srcFile) throws IOException {
		String name = srcFile.getName();
		String extension = FilenameUtils.getExtension(name);
		return File.createTempFile(name, StringUtils.isEmpty(extension) ? "" : '.' + extension, srcFile.getParentFile());
	}

	private static boolean needToResize(MovieMetadatas metadatas, Size maxSize) {
		VideoStream videoStream = metadatas.getVideoStream();
		if(videoStream == null) {
			return false;
		}
		Size size = videoStream.size();
		return ! maxSize.isInside(size);
	}

	private static boolean resizeVideo(MovieMetadatas metadatas, File srcFile, File destFile,
			Size newSize, String consolePrefixMessage) throws IOException {

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(srcFile);
		builder.filter(AutoRotate.create(metadatas));
		builder.filter(Scale.to(newSize, ScaleMode.fitToBoxKeepAspectRatio()));
		OutputProcessor outputProcessor = builder.addMediaOutputFile(destFile);
		outputProcessor.qualityScale(0);
		outputProcessor.codec(H264.findRecommanded().map(c -> c.strict(Strict.EXPERIMENTAL).quality(CRF)).orElse(null));
		outputProcessor.mapAllStreams(inputProcessor);
		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();

		VideoStream videoStream = metadatas.getVideoStream();
		Optional<Integer> countEstimateFrames = videoStream.countEstimateFrames();
		Progress progress = executor.getProgress();
		TextProgressBar textProgressBar = null;
		if(countEstimateFrames.isPresent() && progress != null) {
			textProgressBar = FFMpegProgressBar.with(progress)
					.byFrame(countEstimateFrames.get())
					.fileSize(srcFile.length())
					.build()
					.makeBar(consolePrefixMessage);
		} else {
			StringJoiner joiner = new StringJoiner(", ");
			if(progress == null) {
				joiner.add("progress not found");
			}
			if( ! countEstimateFrames.isPresent()) {
				joiner.add("nb frames nout found");
			}
			System.out.println("No progress bar: " + joiner.toString());
		}

		try {
			executor.execute();
		} finally {
			if(textProgressBar != null) {
				textProgressBar.close();
			}
		}

		Optional<String> codecName = videoStream.codecName();
		if(codecName.isPresent() && codecName.get().equalsIgnoreCase(Formats.HEVC.getName())) { // h265
			return true;
		}
		return false;
	}
}
