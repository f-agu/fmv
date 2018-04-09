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
import org.fagu.fmv.ffmpeg.filter.impl.Delogo;
import org.fagu.fmv.ffmpeg.flags.Strict;
import org.fagu.fmv.ffmpeg.logo.DetectLogo;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.OutputProcessor;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.soft.mediainfo.Info;
import org.fagu.fmv.soft.mediainfo.MediaInfoExtractor;
import org.fagu.fmv.soft.mediainfo.VideoInfo;
import org.fagu.fmv.utils.time.Time;


/**
 * @author Utilisateur
 * @created 4 avr. 2018 18:32:36
 */
public class Reducer implements Closeable {

	private final Logger logger;

	public Reducer(Logger logger) {
		this.logger = logger;
	}

	public void reduce(File srcFile, File destFile, Template template) throws IOException {
		int crf = (int)(getCRF(srcFile).orElse(26));

		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		builder.hideBanner();
		InputProcessor inputProcessor = builder.addMediaInputFile(srcFile);
		Time offsetStart = template.getOffsetStart();
		if(offsetStart != null) {
			inputProcessor.timeSeek(offsetStart);
		}

		Logo logo = template.getLogo();
		if(logo != null) {
			logo = getLogo(srcFile, logo);
			if(logo == null) {
				return; // failed
			}
			Delogo delogo = logo.generateFilter()
					.show(true);
			builder.filter(delogo);
		}

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destFile);
		outputProcessor.qualityScale(0);

		// TODO remove it ! just for debug
		// outputProcessor.duration(Duration.valueOf(60));

		outputProcessor.codec(H264.findRecommanded().strict(Strict.EXPERIMENTAL).quality(crf));

		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();
		logger.log(executor.getCommandLine());
		executor.execute();
	}

	@Override
	public void close() throws IOException {

	}

	// *********************************************************

	private Logo getLogo(File srcFile, Logo logo) throws IOException {
		if( ! logo.isAutoDetect()) {
			logger.log("Logo defined");
			return logo;
		}
		try (DetectLogo detectLogo = DetectLogo.with(srcFile)
				.withTimeSeek(Time.valueOf(60))
				.withLogger(logger::log)
				.build()) {
			Optional<Rectangle> ro = detectLogo.detect();
			if(ro.isPresent()) {
				Rectangle r = ro.get();
				logger.log("Logo found: " + r);
				return Logo.defined(r.x, r.y, r.width, r.height);
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
