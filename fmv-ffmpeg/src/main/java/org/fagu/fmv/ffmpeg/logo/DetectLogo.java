package org.fagu.fmv.ffmpeg.logo;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.impl.SelectVideo;
import org.fagu.fmv.ffmpeg.format.Image2Muxer;
import org.fagu.fmv.ffmpeg.operation.ExtractThumbnail;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.utils.VSync;
import org.fagu.fmv.image.Blur;
import org.fagu.fmv.image.diff.ImageDiff;
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

		private double maxCoverPercent = 0.1D;

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

	public Optional<Rectangle> detect() throws IOException {
		List<File> sceneFiles = extractScenes().getFiles();
		List<File> bwFiles = generateBlackAndWhite(sceneFiles);
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
		if(timeSeek != null) {
			inputProcessor.timeSeek(timeSeek);
		}

		SelectVideo selectVideo = SelectVideo.build().expr("gt(scene\\,0.4)");
		builder.filter(selectVideo);

		builder.mux(Image2Muxer.to(new File(tmpFolder, "scene%05d.png")))
				.videoSync(VSync.PASSTHROUGH);

		FFExecutor<Object> executor = builder.build();
		executor.execute();

		return ExtractThumbnail.find(tmpFolder, "scene\\d+\\.png");
	}

	private List<File> generateBlackAndWhite(List<File> sceneFiles) throws IOException {
		Iterator<File> iterator = sceneFiles.iterator();
		File firstImage = iterator.next();
		int index = 0;
		List<File> bwFiles = new ArrayList<>();
		while(iterator.hasNext()) {
			File nextFile = iterator.next();
			ImageDiff<BufferedImage> imageDiff = ImageDiff.generateImageBlackAndWhite(10);
			BufferedImage compare = imageDiff.compare(firstImage, nextFile);

			compare = Blur.blur(compare, 1F / 4);
			File bwFile = new File(tmpFolder, "bw" + index + ".png");
			ImageIO.write(compare, "png", bwFile);
			bwFiles.add(bwFile);
			++index;
		}
		return bwFiles;
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
		for(int y = 0; y < height; y++) {
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

		File outFile = new File(tmpFolder, "bw-all.png");
		ImageIO.write(out, "png", outFile);
		return outFile;
	}

	private Optional<Rectangle> detectRectangle(File bwAllFile) throws IOException {
		BufferedImage image = ImageIO.read(bwAllFile);
		DetectRectangle detectRectangle = new DetectRectangle(image);
		List<Rectangle> rectangles = detectRectangle.findRectangles();
		// rectangles.forEach(r -> logger.accept("Find rectangle: " + r));
		int x1 = image.getWidth();
		int y1 = image.getHeight();
		int x2 = 0;
		int y2 = 0;
		for(Rectangle rectangle : rectangles) {
			x1 = Math.min(x1, rectangle.x);
			x2 = Math.max(x2, (int)rectangle.getMaxX());
			y1 = Math.min(y1, rectangle.y);
			y2 = Math.max(y2, (int)rectangle.getMaxY());
		}
		if(x1 > x2 || y1 > y2) {
			return Optional.empty();
		}
		Rectangle rect = new Rectangle(x1, y1, x2 - x1, y2 - y1);
		logger.accept("Aggregate to rectangle: " + rect);
		double coverPercent = rect.getWidth() * rect.getHeight() / (image.getWidth() * image.getHeight());
		if(coverPercent > maxCoverPercent) {
			log("Max cover (" + maxCoverPercent + "%) reach: " + coverPercent + "%");
			return Optional.empty();
		}
		return Optional.of(rect);
	}

	private void log(String msg) {
		if(logger != null) {
			logger.accept(msg);
		}
	}
}
