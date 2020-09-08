package org.fagu.fmv.ffmpeg.logo;

/*-
 * #%L
 * fmv-ffmpeg
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.SelectVideo;
import org.fagu.fmv.ffmpeg.format.Image2Muxer;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.ffmpeg.metadatas.VideoStream;
import org.fagu.fmv.ffmpeg.operation.ExtractThumbnail;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.Progress;
import org.fagu.fmv.ffmpeg.progressbar.FFMpegProgressBar;
import org.fagu.fmv.ffmpeg.utils.VSync;
import org.fagu.fmv.image.Blur;
import org.fagu.fmv.image.Rectangle;
import org.fagu.fmv.image.diff.ImageDiff;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.part.ProgressPart;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author Utilisateur
 * @created 9 avr. 2018 15:08:41
 */
public class DetectLogo implements Closeable {

	public static class Builder {

		private final File movieFile;

		private File tmpFolder;

		private Time timeSeek;

		private double maxCoverPercent = 0.05D;

		private Consumer<String> logger;

		private Builder(File movieFile) {
			this.movieFile = movieFile;
			try {
				tmpFolder = Files.createTempDirectory("detectlogo").toFile();
			} catch(IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		public Builder withTmpFolder(File tmpFolder) {
			this.tmpFolder = Objects.requireNonNull(tmpFolder);
			return this;
		}

		public Builder withTimeSeek(Time timeSeek) {
			this.timeSeek = Objects.requireNonNull(timeSeek);
			return this;
		}

		public Builder withMaxCoverPercent(double maxCoverPercent) {
			this.maxCoverPercent = Objects.requireNonNull(maxCoverPercent);
			return this;
		}

		public Builder withLogger(Consumer<String> logger) {
			this.logger = Objects.requireNonNull(logger);
			return this;
		}

		public DetectLogo build() throws IOException {
			return new DetectLogo(this);
		}

	}

	private final File movieFile;

	private final File tmpFolder;

	private final Time timeSeek;

	private final double maxCoverPercent;

	private final Consumer<String> logger;

	private DetectLogo(Builder builder) throws IOException {
		movieFile = builder.movieFile;
		tmpFolder = builder.tmpFolder;
		timeSeek = builder.timeSeek;
		maxCoverPercent = builder.maxCoverPercent;
		logger = builder.logger;
		FileUtils.forceMkdir(tmpFolder);
	}

	public static Builder with(File movieFile) {
		return new Builder(Objects.requireNonNull(movieFile));
	}

	public Detected detect() throws IOException {
		List<File> sceneFiles = extractScenes().getFiles();
		System.out.println();
		List<File> bwFiles = generateBlackAndWhite(sceneFiles);
		System.out.println();
		File bwAllFile = compareAllTogether(bwFiles);
		return detectRectangle(bwAllFile);
	}

	@Override
	public void close() throws IOException {
		FileUtils.forceDelete(tmpFolder);
	}

	// *****************************

	private ExtractThumbnail extractScenes() throws IOException {
		FFMPEGExecutorBuilder builder = FFMPEGExecutorBuilder.create();
		InputProcessor inputProcessor = builder.addMediaInputFile(movieFile);
		MovieMetadatas movieMetadatas = inputProcessor.getMovieMetadatas();
		VideoStream videoStream = movieMetadatas.getVideoStream();
		Duration duration = videoStream.duration().get();
		if(timeSeek != null) {
			inputProcessor.timeSeek(timeSeek);
			duration = duration.add( - timeSeek.toSeconds());
		}

		SelectVideo selectVideo = SelectVideo.build().expr("gt(scene\\,0.4)");
		builder.filter(selectVideo);

		builder.mux(Image2Muxer.to(new File(tmpFolder, "scene%05d.png")))
				.videoSync(VSync.PASSTHROUGH);

		FFExecutor<Object> executor = builder.build();
		logger.accept(executor.getCommandLineString());
		Progress progress = executor.getProgress();
		try (TextProgressBar textProgressBar = FFMpegProgressBar.with(progress)
				.byDuration(duration)
				.build()
				.makeBar("   Extracting scenes")) {

			executor.execute();
		}

		return ExtractThumbnail.find(tmpFolder, "scene\\d+\\.png");
	}

	private List<File> generateBlackAndWhite(List<File> sceneFiles) throws IOException {
		Iterator<File> iterator = sceneFiles.iterator();
		File firstImage = iterator.next();
		AtomicInteger index = new AtomicInteger();
		List<File> bwFiles = new ArrayList<>();
		try (TextProgressBar textProgressBar = TextProgressBar.newBar()
				.fixWidth(60).withText("   Generating B&W")
				.appendText("  ")
				.append(ProgressPart.width(32).build())
				.buildAndSchedule(() -> 100 * index.get() / sceneFiles.size())) {

			while(iterator.hasNext()) {
				File nextFile = iterator.next();
				ImageDiff<BufferedImage> imageDiff = ImageDiff.generateImageBlackAndWhite(10);
				BufferedImage compare = imageDiff.compare(firstImage, nextFile);

				compare = Blur.blur(compare, 1F / 4);
				File bwFile = new File(tmpFolder, "bw" + index.get() + ".png");
				ImageIO.write(compare, "png", bwFile);
				bwFiles.add(bwFile);
				index.incrementAndGet();
			}
			return bwFiles;
		}
	}

	private File compareAllTogether(List<File> bwFiles) throws IOException {
		List<BufferedImage> images = new ArrayList<>();
		for(File file : bwFiles) {
			images.add(ImageIO.read(file));
		}

		final int white = Color.WHITE.getRGB();

		int max = images.size();
		double count;
		BufferedImage firstImage = images.get(0);
		int width = firstImage.getWidth();
		int height = firstImage.getHeight();
		BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		AtomicInteger progressHeight = new AtomicInteger();
		try (TextProgressBar textProgressBar = TextProgressBar.newBar()
				.fixWidth(60).withText("   Comparing")
				.appendText("  ")
				.append(ProgressPart.width(32).build())
				.buildAndSchedule(() -> 100 * progressHeight.get() / height)) {

			for(int y = 0; y < height; y++) {
				progressHeight.set(y);
				for(int x = 0; x < width; x++) {
					count = 0;
					for(BufferedImage image : images) {
						if(image.getRGB(x, y) == white) {
							++count;
						}
					}
					if(count / max > 0.8D) {
						out.setRGB(x, y, white);
					}
				}
			}
		}

		File outFile = new File(tmpFolder, "bw-all.png");
		ImageIO.write(out, "png", outFile);
		return outFile;
	}

	private Detected detectRectangle(File bwAllFile) throws IOException {
		BufferedImage image = ImageIO.read(bwAllFile);
		DetectRectangle detectRectangle = new DetectRectangle(image);
		List<Rectangle> rectangles = detectRectangle.findRectangles();
		if(rectangles.isEmpty()) {
			return Detected.notFound(image.getWidth(), image.getHeight());
		}
		List<Rectangle> endRects = aggregate(rectangles);
		endRects = aggregate(endRects);

		endRects.forEach(r -> log("Aggregate to rectangle: " + r));
		Iterator<Rectangle> iterator = endRects.iterator();
		while(iterator.hasNext()) {
			Rectangle rect = iterator.next();
			double coverPercent = rect.getWidth() * rect.getHeight() / (image.getWidth() * image.getHeight());
			if(coverPercent > maxCoverPercent) {
				log("Max cover (" + maxCoverPercent + "%) reach: " + coverPercent + "%");
				iterator.remove();
			}
		}
		if(endRects.isEmpty()) {
			return Detected.notFound(image.getWidth(), image.getHeight());
		}
		return Detected.found(image.getWidth(), image.getHeight(), endRects);
	}

	private List<Rectangle> aggregate(Collection<Rectangle> rectangles) {
		Set<Rectangle> read = new HashSet<>();

		List<Rectangle> endRects = new ArrayList<>();
		Rectangle curR = null;
		for(Rectangle r1 : rectangles) {
			if(read.contains(r1)) {
				continue;
			}
			curR = r1;
			read.add(r1);
			for(Rectangle r2 : rectangles) {
				if(read.contains(r2)) {
					continue;
				}
				if(curR.intersects(r2) || curR.isGlued(r2)) {
					curR = curR.union(r2).get();
					read.add(r2);
				}
			}
			endRects.add(curR);
		}
		return endRects;
	}

	private void log(String msg) {
		if(logger != null) {
			logger.accept(msg);
		}
	}
}
