package org.fagu.fmv.mymedia.reduce.to720p;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.OptionalInt;
import java.util.StringJoiner;

import org.fagu.fmv.ffmpeg.coder.H264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.AutoRotate;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.metadatas.SubtitleStream;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.progressbar.FFMpegProgressBar;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.movie.StreamOrder;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.utils.media.Size;


/**
 * @author Utilisateur
 * @created 26 mars 2018 09:29:48
 */
public class FFReducer implements Closeable {

	private TextProgressBar textProgressBar;

	/**
	 * @param metadatas
	 * @param srcFile
	 * @param movieMetadatas
	 * @param destFile
	 * @param consolePrefixMessage
	 * @param logger
	 * @throws IOException
	 */
	public void reduceVideo(MovieMetadatas metadatas, File srcFile, MovieMetadatas movieMetadatas, File destFile,
			String consolePrefixMessage, Logger logger) throws IOException {

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(srcFile);
		builder.filter(AutoRotate.create(movieMetadatas));
		org.fagu.fmv.mymedia.reduce.FFReducer.applyScaleIfNecessary(builder, movieMetadatas, Size.HD720, logger);

		MovieMetadatas videoMetadatas = inputProcessor.getMovieMetadatas();
		Collection<AudioStream> audioStreams = StreamOrder.sort(videoMetadatas.getAudioStreams());

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destFile);
		outputProcessor.qualityScale(0);

		// ------------------------ map ------------------------
		// video
		for(Stream stream : videoMetadatas.getVideoStreams()) {
			logger.log("map[" + stream.index() + "] video: " + stream);
			outputProcessor.map().streams(stream).input(inputProcessor);
		}
		// audio
		for(Stream stream : audioStreams) {
			logger.log("map[" + stream.index() + "] audio: " + stream);
			outputProcessor.map().streams(stream).input(inputProcessor);
		}
		// subtitle
		Collection<SubtitleStream> subtitleStreams = StreamOrder.sort(videoMetadatas.getSubtitleStreams());
		for(Stream stream : subtitleStreams) {
			logger.log("map[" + stream.index() + "] subtitle: " + stream);
			outputProcessor.map().streams(stream).input(inputProcessor);
		}

		// -------------------------- codec -------------------------

		outputProcessor.codec(H264.findRecommanded().strict(Strict.EXPERIMENTAL).quality(23));

		// subtitle
		if(videoMetadatas.contains(Type.AUDIO)) {
			outputProcessor.codecCopy(Type.AUDIO);
		}

		// subtitle
		if(videoMetadatas.contains(Type.SUBTITLE)) {
			outputProcessor.codecCopy(Type.SUBTITLE);
		}

		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();

		executor.addListener(org.fagu.fmv.mymedia.reduce.FFReducer.createLogFFExecListener(logger));

		OptionalInt countEstimateFrames = metadatas.getVideoStream().countEstimateFrames();
		Progress progress = executor.getProgress();
		if(countEstimateFrames.isPresent() && progress != null) {
			textProgressBar = FFMpegProgressBar.with(progress).byFrame(countEstimateFrames.getAsInt())
					.fileSize(srcFile.length()).build().makeBar(consolePrefixMessage);
		} else {
			StringJoiner joiner = new StringJoiner(", ");
			if(progress == null) {
				joiner.add("progress not found");
			}
			if( ! countEstimateFrames.isPresent()) {
				joiner.add("nb frames nout found");
			}
			logger.log("No progress bar: " + joiner.toString());
		}

		executor.execute();
	}

	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if(textProgressBar != null) {
			textProgressBar.close();
		}
	}

}
