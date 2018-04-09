package org.fagu.fmv.mymedia.classify.image;

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
import java.util.Arrays;

import org.fagu.fmv.im.Image;
import org.fagu.fmv.mymedia.classify.Organizer;
import org.fagu.fmv.mymedia.file.ImageFinder;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.utils.file.FindProgress;


/**
 * convert IMG_5291.JPG -auto-orient -quality 65% IMG_5291_2.JPG
 *
 * @author fagu
 */
public class Bootstrap {

	// ---------------------------------------------------

	/**
	 * @param saveFile
	 * @param srcFiles
	 * @return
	 * @throws IOException
	 */
	private ImageFinder findImage(File saveFile, File... srcFiles) throws IOException {

		ImageFinder imagesFinder = new ImageFinder(saveFile);
		// imagesFinder.addListener(new FileFinderListener<Image>() {
		//
		// private int count;
		//
		// /**
		// * @see org.fagu.fmv.utils.file.FileFinderListener#eventFind(java.io.File, java.lang.Object)
		// */
		// @Override
		// public void eventFind(File file, Image image) {
		// ImageMetadatas metadatas = image.getMetadatas();
		// String model = metadatas.getDeviceModel();
		// String device = metadatas.getDevice();
		//
		// System.out.println(count + ": " + file.getName() + " : " + device + ", " + model);
		// ++count;
		// }
		// });

		FindProgress findProgress = new FindProgress() {

			private TextProgressBar textProgressBar = TextProgressBar.newBar()
					.fixWidth(40).withText("Finding images")
					.buildForPrinting();

			/**
			 * @see org.fagu.fmv.utils.file.FindProgress#progress(int, int)
			 */
			@Override
			public void progress(int done, int total) {
				textProgressBar.print((100 * done) / total);
			}
		};

		imagesFinder.find(Arrays.asList(srcFiles), findProgress);

		return imagesFinder;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File source = new File(args[0]);

		File saveFile = new File(source, "image.save");
		File destFolder = new File(source.getParentFile(), source.getName() + "-out");

		Bootstrap bootstrap = new Bootstrap();
		try (ImageFinder imageFinder = bootstrap.findImage(saveFile, source)) {
			Organizer<ImageFinder, Image> organizer = new Organizer<>(Image.class);
			organizer.organize(destFolder, imageFinder);
		}
	}
}
