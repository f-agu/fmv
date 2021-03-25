package org.fagu.fmv.mymedia.reduce.neocut;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import org.fagu.fmv.image.Rectangle;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.soft.mediainfo.Info;
import org.fagu.fmv.soft.mediainfo.VideoInfo;
import org.fagu.fmv.soft.mediainfo.raw.RawMediaInfoExtractor;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author Utilisateur
 * @created 4 avr. 2018 18:32:36
 */
public class Reducer implements Closeable {

	private static final int LOGO_PIXEL_SPAN = 3;

	private final Logger logger;

	public Reducer(Logger logger) {
		this.logger = logger;
	}

	public void reduce(File srcFile, File destFile, Template template) throws IOException {
		int crf = (int)(getCRF(srcFile).orElse(26));
		logger.log("CRF: " + crf);

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
			List<Logo> logos = getLogo(srcFile, logo);
			if(logos.isEmpty()) {
				return; // failed, logo not found
			}
			logos.forEach(lg -> builder.filter(lg.generateFilter()));
		}

		Duration endDuration = template.getEndDuration();
		if(endDuration != null) {
			maxDuration = maxDuration.add( - endDuration.toSeconds());
		}

		OutputProcessor outputProcessor = builder.addMediaOutputFile(destFile);
		outputProcessor.qualityScale(0);
		outputProcessor.duration(maxDuration);

		outputProcessor.codec(H264.findRecommanded().map(c -> c.strict(Strict.EXPERIMENTAL).quality(crf)).orElse(null));

		outputProcessor.overwrite();

		FFExecutor<Object> executor = builder.build();
		logger.log(executor.getCommandLineString());

		Progress progress = executor.getProgress();
		try (TextProgressBar textProgressBar = FFMpegProgressBar.with(progress)
				.byDuration(maxDuration)
				.build()
				.makeBar("   Reducing")) {

			executor.execute();
		}
	}

	@Override
	public void close() throws IOException {}

	// *********************************************************

	private List<Logo> getLogo(File srcFile, Logo logo) throws IOException {
		if( ! logo.isAutoDetect()) {
			logger.log("Logo defined: " + logo);
			return Collections.singletonList(logo);
		}
		try (DetectLogo detectLogo = DetectLogo.with(srcFile)
				.withTimeSeek(Time.valueOf(60))
				.withLogger(logger::log)
				.build()) {
			Detected detected = detectLogo.detect();
			Collection<Rectangle> rectangles = detected.getRectangles();
			if(rectangles.isEmpty()) {
				logger.log("Logo not found");
				return Collections.emptyList();
			}
			System.out.println();
			List<Logo> logos = new ArrayList<>();
			for(Rectangle r : rectangles) {
				Logo defined = Logo.defined(
						r.getX() - LOGO_PIXEL_SPAN,
						r.getY() - LOGO_PIXEL_SPAN,
						Math.min(r.getWidth() + 2 * LOGO_PIXEL_SPAN, detected.getMovieWidth()),
						Math.min(r.getHeight() + 2 * LOGO_PIXEL_SPAN, detected.getMovieHeight()));
				logger.log("Logo found: " + defined);
				logos.add(defined);
			}
			return logos;
		}
	}

	private OptionalDouble getCRF(File srcFile) throws IOException {
		RawMediaInfoExtractor extractor = new RawMediaInfoExtractor();
		Info info = extractor.extract(srcFile);
		Optional<VideoInfo> firstVideo = info.getFirstVideo();
		if( ! firstVideo.isPresent()) {
			return OptionalDouble.empty();
		}
		return firstVideo.get().getEncodingSettings().getCRF();
	}

}
