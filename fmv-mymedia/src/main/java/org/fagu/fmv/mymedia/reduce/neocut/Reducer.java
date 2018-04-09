package org.fagu.fmv.mymedia.reduce.neocut;

import java.awt.Rectangle;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalDouble;

import org.fagu.fmv.ffmpeg.coder.H264;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.logo.DetectLogo;
import org.fagu.fmv.ffmpeg.logo.Detected;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.ffmpeg.progressbar.FFMpegProgressBar;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.soft.mediainfo.Info;
import org.fagu.fmv.soft.mediainfo.MediaInfoExtractor;
import org.fagu.fmv.soft.mediainfo.VideoInfo;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author Utilisateur
 * @created 4 avr. 2018 18:32:36
 */
public class Reducer implements Closeable {

	private static final int LOGO_PIXEL_SPAN = 4;

	private final Logger logger;

	public Reducer(Logger logger) {
		this.logger = logger;
	}

	public void reduce(File srcFile, File destFile, Template template) throws IOException {
		int crf = (int)(getCRF(srcFile).orElse(26));

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(srcFile);
		MovieMetadatas movieMetadatas = inputProcessor.getMovieMetadatas();
		VideoStream videoStream = movieMetadatas.getVideoStream();
		Duration maxDuration = videoStream.duration().get();
		Time offsetStart = template.getOffsetStart();
		if(offsetStart != null) {
			inputProcessor.timeSeek(offsetStart);
			maxDuration = maxDuration.add( - offsetStart.toSeconds());
		}

		Logo logo = template.getLogo();
		if(logo != null) {
			logo = getLogo(srcFile, logo);
			if(logo == null) {
				return; // failed, logo not found
			}
			builder.filter(logo.generateFilter());
		}

		Duration endDuration = template.getEndDuration();
		if(endDuration != null) {
			maxDuration = maxDuration.add( - endDuration.toSeconds());
		}

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destFile);
		outputProcessor.qualityScale(0);
		outputProcessor.duration(maxDuration);

		outputProcessor.codec(H264.findRecommanded().strict(Strict.EXPERIMENTAL).quality(crf));

		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();
		logger.log(executor.getCommandLine());

		Progress progress = executor.getProgress();
		try (TextProgressBar textProgressBar = FFMpegProgressBar.with(progress)
				.byDuration(maxDuration)
				.build()
				.makeBar("   Reducing")) {

			executor.execute();
		}
	}

	@Override
	public void close() throws IOException {

	}

	// *********************************************************

	private Logo getLogo(File srcFile, Logo logo) throws IOException {
		if( ! logo.isAutoDetect()) {
			logger.log("Logo defined: " + logo);
			return logo;
		}
		try (DetectLogo detectLogo = DetectLogo.with(srcFile)
				.withTimeSeek(Time.valueOf(60))
				.withLogger(logger::log)
				.build()) {
			Detected detected = detectLogo.detect();
			Optional<Rectangle> ro = detected.getRectangle();
			if(ro.isPresent()) {
				Rectangle r = ro.get();
				Logo defined = Logo.defined(
						r.x - LOGO_PIXEL_SPAN,
						r.y - LOGO_PIXEL_SPAN,
						Math.min(r.width + LOGO_PIXEL_SPAN, detected.getMovieWidth()),
						Math.min(r.height + LOGO_PIXEL_SPAN, detected.getMovieHeight()));
				logger.log("Logo found: " + defined);
				return defined;
			}
			logger.log("Logo not found");
			return null;
		}
	}

	private OptionalDouble getCRF(File srcFile) throws IOException {
		MediaInfoExtractor extractor = new MediaInfoExtractor();
		Info info = extractor.extract(srcFile);
		Optional<VideoInfo> firstVideo = info.getFirstVideo();
		if( ! firstVideo.isPresent()) {
			return OptionalDouble.empty();
		}
		return firstVideo.get().getEncodingSettings().getCRF();
	}

}
