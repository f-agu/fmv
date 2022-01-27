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
import java.util.Optional;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.coder.Decoders;
import org.fagu.fmv.ffmpeg.coder.Decoders.SubType;
import org.fagu.fmv.ffmpeg.coder.H264;
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
import org.fagu.fmv.ffmpeg.format.Formats;
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
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.movie.LoggerFFExecListener;
import org.fagu.fmv.mymedia.movie.StreamOrder;
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

	private static final int CRF = 23;

	private static final List<String> DEFAULT_AUDIO_FORMAT_UNCHANGE = Arrays.asList("");

	private static final List<String> DEFAULT_VIDEO_FORMAT_UNCHANGE = Arrays.asList("mkv");

	private int audioSampleRate = DEFAULT_SAMPLE_RATE;

	private int crf = CRF;

	private String audioFormat;

	private String videoFormat;

	private List<String> audioFormatUnchanges;

	private List<String> videoFormatUnchanges;

	public static void main(String[] args) throws IOException {
		try (FFReducer ffReducer = new FFReducer()) {
			ffReducer.reduceMedia(new File("D:\\tmp\\movie\\Tello\\1545316054015.mp4"),
					"totqdf", Loggers.systemOut());
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
		// videoSampleRate =
		// Integer.parseInt(System.getProperty("fmv.reduce.video.samplerate"));
		// } catch(Exception e) { // ignore
		// }
		// format
		audioFormat = System.getProperty("fmv.reduce.audio.format", DEFAULT_AUDIO_FORMAT);
		videoFormat = System.getProperty("fmv.reduce.video.format", DEFAULT_VIDEO_FORMAT);

		audioFormatUnchanges = getProperyList("fmv.reduce.audio.formatunchange", DEFAULT_AUDIO_FORMAT_UNCHANGE);
		videoFormatUnchanges = getProperyList("fmv.reduce.video.formatunchange", DEFAULT_VIDEO_FORMAT_UNCHANGE);
	}

	@Override
	public String getName() {
		return "FFMpeg";
	}

	@Override
	public Reduced reduceMedia(File srcFile, String consolePrefixMessage, Logger logger) throws IOException {
		File destFile = null;
		boolean forceReplace = false;
		MovieMetadatas metadatas = MovieMetadatas.with(srcFile).extract();
		if(isVideo(metadatas, logger)) {
			logger.log("is video");
			if(needToReduceVideo(metadatas)) {
				destFile = getTempFile(srcFile, getVideoFormat(srcFile));
				forceReplace = reduceVideo(metadatas, srcFile, destFile, consolePrefixMessage, logger);
			} else {
				logger.log("Video already reduced by FMV");
			}

		} else if(metadatas.contains(Type.AUDIO)) {
			logger.log("is audio");
			if(needToReduceAudio(metadatas, srcFile)) {
				List<Stream> onlyStreams = audioOnlyStreams(metadatas);
				destFile = getTempFile(srcFile, getAudioFormat(srcFile));
				forceReplace = reduceAudio(metadatas, srcFile, destFile, "128k", consolePrefixMessage, logger, onlyStreams);
			} else {
				logger.log("Audio already reduced by FMV");
			}
		}
		return new Reduced(destFile, forceReplace);
	}

	public int getCrf() {
		return crf;
	}

	public void setCrf(int crf) {
		this.crf = crf;
	}

	@Override
	public void close() throws IOException {}

	public static Size applyScaleIfNecessary(FFMPEGExecutorBuilder builder, MovieMetadatas movieMetadatas, Size maxSize,
			Logger logger) {
		return applyScaleIfNecessary(builder, movieMetadatas, maxSize, logger, null);
	}

	public static Size applyScaleIfNecessary(FFMPEGExecutorBuilder builder, MovieMetadatas movieMetadatas, Size maxSize,
			Logger logger, Rotation inRotation) {
		VideoStream videoStream = movieMetadatas.getVideoStream();
		Size size = videoStream.size();
		Rotation rotation = inRotation;
		if(rotation == null) {
			rotation = videoStream.rotation();
		}
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

	private String getAudioFormat(File srcFile) {
		return getFormat(srcFile, audioFormatUnchanges, audioFormat);
	}

	private String getVideoFormat(File srcFile) {
		return getFormat(srcFile, videoFormatUnchanges, videoFormat);
	}

	private String getFormat(File srcFile, List<String> formatUnchanges, String defaultValue) {
		String extension = FilenameUtils.getExtension(srcFile.getName());
		return formatUnchanges.stream()//
				.filter(extension::equalsIgnoreCase)//
				.findFirst() //
				.orElse(defaultValue);
	}

	private boolean isVideo(MovieMetadatas metadatas, Logger logger) {
		if( ! metadatas.contains(Type.VIDEO)) {
			logger.log("Is audio: not contains video stream");
			return false;
		}
		return isVideoStream(metadatas.getVideoStream(), logger);
	}

	private boolean isVideoStream(VideoStream videoStream, Logger logger) {
		Optional<Integer> numberOfFrames = videoStream.numberOfFrames();
		if(numberOfFrames.isPresent() && numberOfFrames.get() == 1) {
			logger.log("Is audio: number of frames unavailable: " + numberOfFrames);
			return false;
		}
		String name = videoStream.codecName().get();
		logger.log("Video codec is " + name);
		if(Decoders.byName(name).is(SubType.IMAGE)) {
			// FrameRate frameRate = videoStream.frameRate().orElse(null);
			// if(frameRate != null && frameRate.floatValue() < 100) {
			// return true;
			// }
			// logger.log("Is audio: frameRate is null or too high (" + frameRate + ") AND video codec is " + name);
			return false;
		}
		return true;
	}

	private boolean needToReduceVideo(MovieMetadatas metadatas) {
		return ! metadatas.isTreatedByFMV();
	}

	private boolean reduceVideo(MovieMetadatas metadatas, File srcFile, File destFile,
			String consolePrefixMessage, Logger logger) throws IOException {

		AudioStream audioStream = metadatas.getAudioStream();
		boolean hasAudio = audioStream != null;
		boolean audioCodecCopy = hasAudio && audioStream.isCodec(Formats.AC3);

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(srcFile);
		builder.filter(AutoRotate.create(metadatas));
		applyScaleIfNecessary(builder, metadatas, getMaxSize(), logger);
		VolumeDetect volumeDetect = null;
		if(hasAudio && ! audioCodecCopy) {
			volumeDetect = VolumeDetect.build();
			builder.filter(volumeDetect);
		}
		CropDetect cropDetect = CropDetect.build();
		builder.filter(cropDetect);

		MovieMetadatas videoMetadatas = inputProcessor.getMovieMetadatas();
		Collection<AudioStream> audioStreams = StreamOrder.sort(videoMetadatas.getAudioStreams());

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destFile);
		outputProcessor.qualityScale(0);

		// ------------------------ map ------------------------
		// video
		for(VideoStream stream : videoMetadatas.getVideoStreams()) {
			boolean isVideo = isVideoStream(stream, logger);
			logger.log("map[" + stream.index() + "] video: " + stream + (isVideo ? "" : " (not included, it's not really a video)"));
			if(isVideo) {
				outputProcessor.map().streams(stream).input(inputProcessor);
			}
		}
		// audio
		for(Stream stream : audioStreams) {
			logger.log("map[" + stream.index() + "] audio: " + stream);
			outputProcessor.map().streams(stream).input(inputProcessor);
		}
		// subtitle
		Collection<SubtitleStream> subtitleStreams = StreamOrder.sort(videoMetadatas.getSubtitleStreams());
		for(Stream stream : subtitleStreams) {
			outputProcessor.map().streams(stream).input(inputProcessor);
			Optional<String> codecName = stream.codecName();
			if(codecName.isPresent() && codecName.get().equals("subrip")) {
				File subtitleOutputFile = getSubtitleOutputFile(srcFile, stream);
				logger.log("map[" + stream.index() + "] subtitle: " + stream + "  => " + subtitleOutputFile.getName());

				builder.addMediaOutputFile(subtitleOutputFile)
						.map().streams(stream).input(inputProcessor);
			} else {
				logger.log("map[" + stream.index() + "] subtitle: " + stream + " (" + stream.codecName().orElse("?") + ")");
			}
		}
		// other stream (Apple... again bullshit)
		// for (Stream stream : videoMetadatas.getStreams()) {
		// Type type = stream.type();
		// if (type != Type.AUDIO && type != Type.VIDEO && type != Type.SUBTITLE) {
		// logger.log("map other stream: " + stream);
		// outputProcessor.map().streams(stream).input(inputProcessor);
		// }
		// }

		// -------------------------- codec -------------------------

		outputProcessor.codec(H264.findRecommanded().map(c -> c.strict(Strict.EXPERIMENTAL).quality(crf)).orElse(null));

		// audio
		if(hasAudio) {
			if(audioCodecCopy) {
				logger.log("Audio: AC3, copy");
				outputProcessor.codecCopy(Type.AUDIO);
			} else {
				logger.log("Audio: force AAC");
				outputProcessor.codecAutoSelectAAC();
			}
		}

		// subtitle
		if( ! subtitleStreams.isEmpty()) {
			outputProcessor.codecCopy(Type.SUBTITLE);
		}

		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();

		executor.addListener(new LoggerFFExecListener(logger));
		executor.addListener(createCropDetectFFExecListener(logger, cropDetect, videoMetadatas));
		if( ! audioCodecCopy) {
			executor.addListener(createVolumeDetectFFExecListener(logger, volumeDetect));
		}

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
			logger.log("No progress bar: " + joiner.toString());
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

	private File getSubtitleOutputFile(File srcFile, Stream subtitleStream) {
		StringBuilder joiner = new StringBuilder("")
				.append(FilenameUtils.getBaseName(srcFile.getName()));
		subtitleStream.language().ifPresent(lg -> joiner.append('-').append(lg));
		subtitleStream.title().ifPresent(t -> joiner.append('-').append(t));
		joiner.append('-').append(subtitleStream.index()).append(".srt");
		return new File(srcFile.getParentFile(), joiner.toString());
	}

	private boolean needToReduceAudio(MovieMetadatas metadatas, File srcFile) {
		String extension = FilenameUtils.getExtension(srcFile.getName());
		if( ! audioFormat.equalsIgnoreCase(extension)) {
			return true;
		}
		if(metadatas.getAudioStreams().stream()
				.anyMatch(audioStream -> audioStream.bitRate().isPresent() && audioStream.bitRate().get() > 128_000)) {
			return true;
		}
		if(metadatas.getVideoStreams().stream()
				.anyMatch(videoStream -> Decoders.MJPEG.getName().equals(videoStream.codecName().get()))) {
			return true; // has cover
		}
		return false;
	}

	private List<Stream> audioOnlyStreams(MovieMetadatas metadatas) {
		List<AudioStream> audioStreams = metadatas.getAudioStreams();
		List<Stream> selected = audioStreams.stream()
				.filter(audioStream -> {
					if( ! audioStream.bitRate().isPresent()) {
						System.out.println(audioStream);
						return false;
					}
					// if( ! audioStream.frameRate().isPresent()) {
					// return false;
					// }
					return true;
				})
				.collect(Collectors.toList());
		return selected.size() == audioStreams.size() ? null : selected;
	}

	private boolean reduceAudio(MovieMetadatas metadatas, File srcFile, File destFile, String bitRate,
			String consolePrefixMessage, Logger logger, Collection<Stream> onlyStreams) throws IOException {

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

		if(onlyStreams != null) {
			outputProcessor.map().streams(onlyStreams).input(filter);
		} else {
			outputProcessor.map().allStreams().input(filter);
		}

		FFExecutor<Object> executor = builder.build();
		executor.addListener(new LoggerFFExecListener(logger));
		Duration duration = metadatas.getAudioStream().duration().orElse(null);
		Progress progress = executor.getProgress();
		TextProgressBar textProgressBar = null;
		if(duration != null && progress != null) {
			textProgressBar = FFMpegProgressBar.with(progress)
					.byDuration(duration)
					.build()
					.makeBar(consolePrefixMessage);
		}
		try {
			executor.execute();
		} finally {
			if(textProgressBar != null) {
				textProgressBar.close();
			}
		}

		return false;
	}

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

	private FFExecListener createCropDetectFFExecListener(Logger logger, CropDetect cropDetect,
			MovieMetadatas videoMetadatas) {
		return new FFExecListener() {

			@Override
			public void eventPostExecute(FMVExecutor fmvExecutor, CommandLine command, Map environment,
					ExecuteResultHandler handler) {
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

	private FFExecListener createVolumeDetectFFExecListener(Logger logger, VolumeDetect volumeDetect) {
		return new FFExecListener() {

			@Override
			public void eventPostExecute(FMVExecutor fmvExecutor, CommandLine command, Map environment,
					ExecuteResultHandler handler) {
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

	private Size getMaxSize() {
		String property = System.getProperty("fmv.reduce.video.maxsize");
		if(property == null) {
			return MAX_SIZE;
		}
		return Size.parse(property);
	}
}
