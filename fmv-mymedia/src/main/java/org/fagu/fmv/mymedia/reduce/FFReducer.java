package org.fagu.fmv.mymedia.reduce;

/*
 * #%L
 * fmv-mymedia
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.StringTokenizer;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.coder.Decoders;
import org.fagu.fmv.ffmpeg.coder.Libx264;
import org.fagu.fmv.ffmpeg.executor.FFExecFallback;
import org.fagu.fmv.ffmpeg.executor.FFExecListener;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.executor.fallback.Libx264NotDisibleBy2FFExecFallback;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.impl.AutoRotate;
import org.fagu.fmv.ffmpeg.filter.impl.CropDetect;
import org.fagu.fmv.ffmpeg.filter.impl.CropDetection;
import org.fagu.fmv.ffmpeg.filter.impl.CropDetection.CropSize;
import org.fagu.fmv.ffmpeg.filter.impl.ResampleAudio;
import org.fagu.fmv.ffmpeg.filter.impl.Scale;
import org.fagu.fmv.ffmpeg.filter.impl.ScaleMode;
import org.fagu.fmv.ffmpeg.filter.impl.VolumeDetect;
import org.fagu.fmv.ffmpeg.filter.impl.VolumeDetected;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.metadatas.AudioStream;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.Stream;
import org.fagu.fmv.ffmpeg.metadatas.SubtitleStream;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.progressbar.FFMpegProgressBar;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.movie.StreamOrder;
import org.fagu.fmv.soft.exec.CommandLineUtils;
import org.fagu.fmv.soft.exec.FMVExecutor;
import org.fagu.fmv.soft.exec.exception.FMVExecuteException;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.utils.media.Rotation;
import org.fagu.fmv.utils.media.Size;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public class FFReducer extends AbstractReducer {

	private static final int DEFAULT_SAMPLE_RATE = 44100;

	private static final String DEFAULT_AUDIO_FORMAT = "mp3";

	private static final String DEFAULT_VIDEO_FORMAT = "mp4";

	private static final Size MAX_SIZE = Size.HD1080;

	private static final List<String> DEFAULT_AUDIO_FORMAT_UNCHANGE = Arrays.asList("");

	private static final List<String> DEFAULT_VIDEO_FORMAT_UNCHANGE = Arrays.asList("mkv");

	private int audioSampleRate = DEFAULT_SAMPLE_RATE;

	// , videoSampleRate = DEFAULT_SAMPLE_RATE;

	private String audioFormat, videoFormat;

	private List<String> audioFormatUnchanges, videoFormatUnchanges;

	private TextProgressBar textProgressBar;

	public static void main(String[] args) throws IOException {
		try (FFReducer ffReducer = new FFReducer()) {
			ffReducer.reduceMedia(new File(
					"D:\\tmp\\dvd-rip\\test\\a\\a.vob"), "totogsdlfjhsdkfjhqsdfiohqsdohqsjhbqsdjhbqsdgjhqbsdgoqbrgioqdbgqdfjkgbgqdfjgbgbqdfjkgbqdfjgbqdf", Loggers
							.noOperation());
		} catch(FMVExecuteException e) {
			e.printStackTrace();
			// if( ! e.isKnown()) {
			// throw e;
			// }
			// System.out.println("Error: " + e.getExceptionKnown().get().toString());
		}
	}

	/**
	 *
	 */
	public FFReducer() {
		try {
			audioSampleRate = Integer.parseInt(System.getProperty("fmv.reduce.audio.samplerate"));
		} catch(Exception e) {// ignore
		}
		// try {
		// videoSampleRate = Integer.parseInt(System.getProperty("fmv.reduce.video.samplerate"));
		// } catch(Exception e) { // ignore
		// }
		// format
		audioFormat = System.getProperty("fmv.reduce.audio.format", DEFAULT_AUDIO_FORMAT);
		videoFormat = System.getProperty("fmv.reduce.video.format", DEFAULT_VIDEO_FORMAT);

		audioFormatUnchanges = getProperyList("fmv.reduce.audio.formatunchange", DEFAULT_AUDIO_FORMAT_UNCHANGE);
		videoFormatUnchanges = getProperyList("fmv.reduce.video.formatunchange", DEFAULT_VIDEO_FORMAT_UNCHANGE);
	}

	/**
	 * @see org.fagu.fmv.mymedia.reduce.Reducer#getName()
	 */
	@Override
	public String getName() {
		return "FFMpeg";
	}

	/**
	 * @see org.fagu.fmv.mymedia.reduce.Reducer#reduceMedia(java.io.File, String, Logger)
	 */
	@Override
	public File reduceMedia(File srcFile, String consolePrefixMessage, Logger logger) throws IOException {
		File destFile = null;
		MovieMetadatas metadatas = MovieMetadatas.with(srcFile).extract();
		if(isVideo(metadatas, logger)) {
			logger.log("is video");
			if(needToReduceVideo(metadatas)) {
				destFile = getTempFile(srcFile, getVideoFormat(srcFile));
				reduceVideo(metadatas, srcFile, metadatas, destFile, consolePrefixMessage, logger);
			} else {
				logger.log("Video already reduced by FMV");
			}

		} else if(metadatas.contains(Type.AUDIO)) {
			logger.log("is audio");
			if(needToReduceAudio(metadatas, srcFile)) {
				destFile = getTempFile(srcFile, getAudioFormat(srcFile));
				reduceAudio(metadatas, srcFile, destFile, "128k", consolePrefixMessage, logger);
			} else {
				logger.log("Audio already reduced by FMV");
			}
		}
		return destFile;
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

	/**
	 * @param builder
	 * @param movieMetadatas
	 * @param maxSize
	 * @param logger
	 */
	public static Size applyScaleIfNecessary(FFMPEGExecutorBuilder builder, MovieMetadatas movieMetadatas, Size maxSize, Logger logger) {
		VideoStream videoStream = movieMetadatas.getVideoStream();
		Size size = videoStream.size();
		Rotation rotation = videoStream.rotate();
		if(rotation != null) {
			size = rotation.resize(size);
		}
		if(size.getWidth() <= maxSize.getWidth() && size.getHeight() <= maxSize.getHeight()) {
			return size;
		}
		StringBuilder log = new StringBuilder();
		log.append("Need to resize ").append(size);
		if(rotation != null) {
			log.append(" (rotation of ").append(rotation.getValue()).append(')');
		}
		size = size.fitAndKeepRatioTo(maxSize);
		if(rotation != null && ! AutoRotate.isAutoRotateObsolete()) {
			size = rotation.resize(size);
		}

		// fix [libx264 @ 037f8b00] height not divisible by 2 (1078x607)
		size = Libx264NotDisibleBy2FFExecFallback.resize(size);

		log.append(" to under ").append(maxSize).append(": ").append(size);
		logger.log(log.toString());

		builder.filter(Scale.to(size, ScaleMode.fitToBoxKeepAspectRatio())); // .forceOriginalAspectRatio(ForceOriginalAspectRatio.DECREASE)
		return size;
	}

	// *******************************************

	/**
	 * @return
	 */
	private String getAudioFormat(File srcFile) {
		return getFormat(srcFile, audioFormatUnchanges, audioFormat);
	}

	/**
	 * @return
	 */
	private String getVideoFormat(File srcFile) {
		return getFormat(srcFile, videoFormatUnchanges, videoFormat);
	}

	/**
	 * @return
	 */
	private String getFormat(File srcFile, List<String> formatUnchanges, String defaultValue) {
		String extension = FilenameUtils.getExtension(srcFile.getName());
		return formatUnchanges.stream()//
				.filter(extension::equalsIgnoreCase)//
				.findFirst() //
				.orElse(defaultValue);
	}

	/**
	 * @param metadatas
	 * @param logger
	 * @return
	 */
	private boolean isVideo(MovieMetadatas metadatas, Logger logger) {
		if( ! metadatas.contains(Type.VIDEO)) {
			logger.log("Is audio: not contains video stream");
			return false;
		}
		VideoStream videoStream = metadatas.getVideoStream();
		OptionalInt numberOfFrames = videoStream.numberOfFrames();
		if(numberOfFrames.isPresent() && numberOfFrames.getAsInt() == 1) {
			logger.log("Is audio: number of frames unavailable: " + numberOfFrames);
			return false;
		}
		if(Decoders.MJPEG.getName().equals(videoStream.codecName().get())) {
			FrameRate frameRate = videoStream.frameRate().orElse(null);
			if(frameRate != null && frameRate.floatValue() < 100) {
				return true;
			}
			logger.log("Is audio: frameRate is null or too high: " + frameRate);
			return false;
		}
		return true;
	}

	/**
	 * @param metadatas
	 * @return
	 */
	private boolean needToReduceVideo(MovieMetadatas metadatas) {
		return ! metadatas.isTreatedByFMV();
	}

	/**
	 * @param metadatas
	 * @param srcFile
	 * @param movieMetadatas
	 * @param destFile
	 * @param consolePrefixMessage
	 * @param logger
	 * @throws IOException
	 */
	private void reduceVideo(MovieMetadatas metadatas, File srcFile, MovieMetadatas movieMetadatas, File destFile, String consolePrefixMessage,
			Logger logger) throws IOException {

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(srcFile);
		builder.filter(AutoRotate.create(movieMetadatas));
		applyScaleIfNecessary(builder, movieMetadatas, getMaxSize(), logger);
		VolumeDetect volumeDetect = VolumeDetect.build();
		builder.filter(volumeDetect);
		CropDetect cropDetect = CropDetect.build();
		builder.filter(cropDetect);

		MovieMetadatas videoMetadatas = inputProcessor.getMovieMetadatas();
		Collection<AudioStream> audioStreams = StreamOrder.sort(videoMetadatas.getAudioStreams());

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destFile);
		outputProcessor.qualityScale(0);

		// ------------------------ map ------------------------
		// video
		for(Stream stream : videoMetadatas.getVideoStreams()) {
			logger.log("map video: " + stream);
			outputProcessor.map().streams(stream).input(inputProcessor);
		}
		// audio
		for(Stream stream : audioStreams) {
			logger.log("map audio: " + stream);
			outputProcessor.map().streams(stream).input(inputProcessor);
		}
		// subtitle
		Collection<SubtitleStream> subtitleStreams = StreamOrder.sort(videoMetadatas.getSubtitleStreams());
		for(Stream stream : subtitleStreams) {
			logger.log("map subtitle: " + stream);
			outputProcessor.map().streams(stream).input(inputProcessor);
		}
		// other stream
		for(Stream stream : videoMetadatas.getStreams()) {
			Type type = stream.type();
			if(type != Type.AUDIO && type != Type.VIDEO && type != Type.SUBTITLE) {
				logger.log("map other stream: " + stream);
				outputProcessor.map().streams(stream).input(inputProcessor);
			}
		}

		// -------------------------- codec -------------------------

		outputProcessor.codec(Libx264.build().strict(Strict.EXPERIMENTAL).crf(23));

		// audio
		outputProcessor.codecAutoSelectAAC();

		// subtitle
		if(videoMetadatas.contains(Type.SUBTITLE)) {
			outputProcessor.codecCopy(Type.SUBTITLE);
		}

		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();

		executor.addListener(createLogFFExecListener(logger));
		executor.addListener(createCropDetectFFExecListener(logger, cropDetect, videoMetadatas));
		executor.addListener(createVolumeDetectFFExecListener(logger, volumeDetect));

		OptionalInt countEstimateFrames = metadatas.getVideoStream().countEstimateFrames();
		Progress progress = executor.getProgress();
		if(countEstimateFrames.isPresent() && progress != null) {
			textProgressBar = FFMpegProgressBar.with(progress)
					.byFrame(countEstimateFrames.getAsInt())
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
			logger.log("No progress bar: " + joiner.toString());
		}

		executor.execute();
	}

	/**
	 * @param metadatas
	 * @param srcFile
	 * @return
	 */
	private boolean needToReduceAudio(MovieMetadatas metadatas, File srcFile) {
		AudioStream audioStream = metadatas.getAudioStream();
		String extension = FilenameUtils.getExtension(srcFile.getName());
		if( ! audioFormat.equalsIgnoreCase(extension) || audioStream.bitRate().getAsInt() > 128000) {
			return true;
		}
		VideoStream videoStream = metadatas.getVideoStream();
		if(videoStream != null && Decoders.MJPEG.getName().equals(videoStream.codecName().get())) {
			return true; // has cover
		}
		return false;
	}

	/**
	 * @param metadatas
	 * @param srcFile
	 * @param destFile
	 * @param bitRate
	 * @param consolePrefixMessage
	 * @param logger
	 * @throws IOException
	 */
	private void reduceAudio(MovieMetadatas metadatas, File srcFile, File destFile, String bitRate, String consolePrefixMessage, Logger logger)
			throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();

		int sampleRate = Math.min(metadatas.getAudioStream().sampleRate().orElse(audioSampleRate), audioSampleRate);
		builder.addMediaInputFile(srcFile);

		FilterComplex filter = FilterComplex.create(ResampleAudio.build().frequency(sampleRate));
		builder.filter(filter);

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destFile);
		outputProcessor.audioChannel(2);
		outputProcessor.audioBitRate(bitRate);
		outputProcessor.format(audioFormat);
		outputProcessor.overwrite();

		outputProcessor.map().allStreams().input(filter);

		FFExecutor<Object> executor = builder.build();
		executor.addListener(createLogFFExecListener(logger));
		Duration duration = metadatas.getAudioStream().duration().orElse(null);
		Progress progress = executor.getProgress();
		if(duration != null && progress != null) {
			textProgressBar = FFMpegProgressBar.with(progress)
					.byDuration(duration)
					.fileSize(srcFile.length())
					.build()
					.makeBar(consolePrefixMessage);
		}
		executor.execute();
	}

	/**
	 * @param propertyKey
	 * @param defaultValues
	 * @return
	 */
	private List<String> getProperyList(String propertyKey, List<String> defaultValues) {
		String values = System.getProperty(propertyKey);
		if(values == null) {
			return defaultValues;
		}
		StringTokenizer stringTokenizer = new StringTokenizer(values, ",;");
		List<String> list = new ArrayList<>();
		while(stringTokenizer.hasMoreTokens()) {
			String value = stringTokenizer.nextToken().trim();
			if(StringUtils.isNotBlank(value)) {
				list.add(value);
			}
		}
		return list;
	}

	/**
	 * @param logger
	 * @return
	 */
	private FFExecListener createLogFFExecListener(Logger logger) {
		return new FFExecListener() {

			/**
			 * @see org.fagu.fmv.utils.exec.FMVExecListener#eventPreExecute(org.fagu.fmv.utils.exec.FMVExecutor,
			 *      org.apache.commons.exec.CommandLine, java.util.Map, org.apache.commons.exec.ExecuteResultHandler)
			 */
			@Override
			public void eventPreExecute(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
					ExecuteResultHandler handler) {
				logger.log("Exec: " + CommandLineUtils.toLine(command));
			}

			/**
			 * @see org.fagu.fmv.ffmpeg.executor.FFExecListener#eventPreExecFallbacks(org.apache.commons.exec.CommandLine,
			 *      java.util.Collection)
			 */
			@Override
			public void eventPreExecFallbacks(CommandLine command, Collection<FFExecFallback> fallbacks) {
				logger.log("Exec fallback " + fallbacks + ": " + CommandLineUtils.toLine(command));
			}

			/**
			 * @see org.fagu.fmv.ffmpeg.executor.FFExecListener#eventFallbackNotFound(org.fagu.fmv.soft.exec.FMVExecutor,
			 *      org.apache.commons.exec.CommandLine)
			 */
			@Override
			public void eventFallbackNotFound(CommandLine command, List<String> outputs) {
				logger.log("Fallback not found: " + CommandLineUtils.toLine(command));
				outputs.forEach(logger::log);
			}

		};
	}

	/**
	 * @param logger
	 * @param cropDetect
	 * @param videoMetadatas
	 * @return
	 */
	private FFExecListener createCropDetectFFExecListener(Logger logger, CropDetect cropDetect, MovieMetadatas videoMetadatas) {
		return new FFExecListener() {

			/**
			 * @see org.fagu.fmv.soft.exec.FMVExecListener#eventPostExecute(org.fagu.fmv.soft.exec.FMVExecutor,
			 *      org.apache.commons.exec.CommandLine, java.util.Map, org.apache.commons.exec.ExecuteResultHandler)
			 */
			@Override
			public void eventPostExecute(FMVExecutor fmvExecutor, CommandLine command, Map environment, ExecuteResultHandler handler) {
				CropDetection cropDetection = cropDetect.getCropSizeDetected();
				SortedSet<CropSize> orderedCropSizes = cropDetection.getOrderedCropSizes();
				if( ! orderedCropSizes.isEmpty()) {
					CropSize first = orderedCropSizes.first();
					Size size = first.toSize();

					if( ! videoMetadatas.getVideoStreams().stream().anyMatch(s -> size.equals(s.size()))) {
						logger.log("CropDetect: " + cropDetection.getTotalCount() + " lines parsed");
						orderedCropSizes.stream().limit(10).forEach(cs -> logger.log("CropDetect: " + cs));
						logger.log("CropDetect: Add crop filter: " + first.toCrop());
					}
				}
			}

		};
	}

	/**
	 * @param logger
	 * @param volumeDetect
	 * @return
	 */
	private FFExecListener createVolumeDetectFFExecListener(Logger logger, VolumeDetect volumeDetect) {
		return new FFExecListener() {

			/**
			 * @see org.fagu.fmv.soft.exec.FMVExecListener#eventPostExecute(org.fagu.fmv.soft.exec.FMVExecutor,
			 *      org.apache.commons.exec.CommandLine, java.util.Map, org.apache.commons.exec.ExecuteResultHandler)
			 */
			@Override
			public void eventPostExecute(FMVExecutor fmvExecutor, CommandLine command, Map environment, ExecuteResultHandler handler) {
				if(volumeDetect.isDetected()) {
					VolumeDetected detected = volumeDetect.getDetected();
					logger.log("VolumeDetect: nb_sample= " + detected.countSample());
					logger.log("VolumeDetect: max=       " + detected.getMax());
					logger.log("VolumeDetect: mean=      " + detected.getMean());
					logger.log("VolumeDetect: histogram= " + detected.getHistogram());
					logger.log("VolumeDetect: Add volume filter: " + detected.toMaxVolume());
				} else {
					logger.log("volume not detected");
				}

			}

		};
	}

	/**
	 * @return
	 */
	private Size getMaxSize() {
		String property = System.getProperty("fmv.reduce.video.maxsize");
		if(property == null) {
			return MAX_SIZE;
		}
		return Size.parse(property);
	}
}
