package org.fagu.fmv.mymedia.compare;

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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.imageio.ImageIO;


/**
 * @author f.agu
 * @created 3 avr. 2018 22:55:42
 */
public class ImageDiff<O> {

	// ---------------------------------------------

	public interface ImageNotSameSize<O> {

		O create(int width1, int height1, int width2, int height2);

	}

	// ---------------------------------------------

	public interface PixelComparator<O> {

		void init(BufferedImage img1, BufferedImage img2);

		default void compute(int x, int y, int r1, int g1, int b1, int r2, int g2, int b2) {
			int r = Math.abs(r1 - r2);
			int g = Math.abs(g1 - g2);
			int b = Math.abs(b1 - b2);
			compute(x, y, r, g, b);
		}

		void compute(int x, int y, int r, int g, int b);

		O get();
	}

	// ---------------------------------------------

	private final ImageNotSameSize<O> imageNotSameSize;

	private final PixelComparator<O> pixelComparator;

	/**
	 * @param imageNotSameSize
	 * @param pixelComparator
	 */
	public ImageDiff(ImageNotSameSize<O> imageNotSameSize, PixelComparator<O> pixelComparator) {
		this.imageNotSameSize = Objects.requireNonNull(imageNotSameSize);
		this.pixelComparator = Objects.requireNonNull(pixelComparator);
	}

	/**
	 * @return
	 */
	public static ImageDiff<Double> percent() {
		return new ImageDiff<>((w1, h1, w2, h2) -> 100D,
				new PixelComparator<Double>() {

					private long diff = 0;

					private long maxDiff;

					@Override
					public void init(BufferedImage img1, BufferedImage img2) {
						maxDiff = 3L * 255 * img1.getWidth() * img1.getHeight();
					}

					@Override
					public Double get() {
						return 100.0 * diff / maxDiff;
					}

					@Override
					public void compute(int x, int y, int r, int g, int b) {
						diff += r + g + b;
					}

				});
	}

	// ---------------------------------------------

	public interface ImageGenerator {

		ImageGenerator init(int width, int height);

		ImageGenerator set(int x, int y, int r, int g, int b);

		BufferedImage generate();

	}

	// ---------------------------------------------

	public abstract static class BasicImageGenerator implements ImageGenerator {

		protected BufferedImage output;

		@Override
		public ImageGenerator init(int width, int height) {
			output = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
			return this;
		}

		@Override
		public BufferedImage generate() {
			return output;
		}

	};

	// ---------------------------------------------

	/**
	 * @return
	 */
	public static ImageDiff<BufferedImage> generateImage(ImageGenerator imageGenerator) {
		return new ImageDiff<>((w1, h1, w2, h2) -> imageGenerator.init(w1, h1).generate(),
				new PixelComparator<BufferedImage>() {

					@Override
					public void init(BufferedImage img1, BufferedImage img2) {
						imageGenerator.init(img1.getWidth(), img1.getHeight());
					}

					@Override
					public void compute(int x, int y, int r, int g, int b) {
						imageGenerator.set(x, y, r, g, b);
					}

					@Override
					public BufferedImage get() {
						return imageGenerator.generate();
					}
				});
	}

	/**
	 * @return
	 */
	public static ImageDiff<BufferedImage> generateImageBlackAndWhite(int tolerance) {
		return generateImage(new BasicImageGenerator() {

			@Override
			public ImageGenerator set(int x, int y, int r, int g, int b) {
				Color color = Math.abs(r) > tolerance || Math.abs(g) > tolerance || Math.abs(b) > tolerance ? Color.BLACK : Color.WHITE;
				output.setRGB(x, y, color.getRGB());
				return this;
			}
		});
	}

	/**
	 * @param f1
	 * @param f2
	 * @return
	 * @throws IOException
	 */
	public O compare(File f1, File f2) throws IOException {
		BufferedImage img1 = ImageIO.read(f1);
		BufferedImage img2 = ImageIO.read(f2);
		return compare(img1, img2);
	}

	/**
	 * @param i1
	 * @param i2
	 * @return
	 * @throws IOException
	 */
	public O compare(InputStream i1, InputStream i2) throws IOException {
		BufferedImage img1 = ImageIO.read(i1);
		BufferedImage img2 = ImageIO.read(i2);
		return compare(img1, img2);
	}

	/**
	 * From http://rosettacode.org/wiki/Percentage_difference_between_images
	 * 
	 * @param img1
	 * @param img2
	 * @return
	 */
	public O compare(BufferedImage img1, BufferedImage img2) {
		int width1 = img1.getWidth();
		int height1 = img1.getHeight();
		int width2 = img2.getWidth();
		int height2 = img2.getHeight();
		if(width1 != width2 || height1 != height2) {
			return imageNotSameSize.create(width1, height1, width2, height2);
		}
		pixelComparator.init(img1, img2);
		for(int y = 0; y < height1; y++) {
			for(int x = 0; x < width1; x++) {
				pixelDiff(x, y, img1.getRGB(x, y), img2.getRGB(x, y));
			}
		}
		return pixelComparator.get();
	}

	// **********************************************

	/**
	 * @param x
	 * @param y
	 * @param rgb1
	 * @param rgb2
	 */
	private void pixelDiff(int x, int y, int rgb1, int rgb2) {
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >> 8) & 0xff;
		int b1 = rgb1 & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >> 8) & 0xff;
		int b2 = rgb2 & 0xff;
		pixelComparator.compute(x, y, r1, g1, b1, r2, g2, b2);
	}
}
