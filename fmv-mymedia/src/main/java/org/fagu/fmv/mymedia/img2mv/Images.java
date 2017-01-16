package org.fagu.fmv.mymedia.img2mv;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.fagu.fmv.image.Image;
import org.fagu.fmv.mymedia.classify.Classifier;
import org.fagu.fmv.mymedia.classify.ClassifierFactories;
import org.fagu.fmv.mymedia.classify.ConverterListener;
import org.fagu.fmv.mymedia.classify.image.ReduceImageConverter;
import org.fagu.fmv.mymedia.file.ImageFinder;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.media.Size;


/**
 * @author f.agu
 */
public class Images {

	private ImageFinder imageFinder;

	private File sourceFolder;

	/**
	 * @param sourceFolder
	 * @param imageFinder
	 */
	private Images(File sourceFolder, ImageFinder imageFinder) {
		this.sourceFolder = sourceFolder;
		this.imageFinder = imageFinder;
	}

	/**
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public static Images find(File folder) throws IOException {
		try (ImageFinder imageFinder = new ImageFinder(new File(folder, "image.save"))) {
			imageFinder.find(folder);
			return new Images(folder, imageFinder);
		}
	}

	/**
	 * @return the imageList
	 */
	public Collection<FileFinder<Image>.InfosFile> getImages() {
		return Collections.unmodifiableCollection(imageFinder.getAll());
	}

	/**
	 * @param size
	 */
	public PreparedImages prepare(Size size) throws IOException {
		final int COUNT_TOTAL = getImages().size();
		File destFolder = new File(sourceFolder.getParent(), sourceFolder.getName() + "-img2mov");
		if(destFolder.exists()) {
			File[] listFiles = destFolder.listFiles(f -> f.getName().startsWith("img_"));
			if(listFiles.length == COUNT_TOTAL) {
				return new PreparedImages(Arrays.asList(listFiles));
			}
		}
		final AtomicInteger count = new AtomicInteger();
		ConverterListener<Image> converterListener = new ConverterListener<Image>() {

			/**
			 * @see org.fagu.fmv.mymedia.classify.ConverterListener#eventPreConvert(org.fagu.fmv.media.Media,
			 *      java.io.File)
			 */
			@Override
			public void eventPreConvert(Image srcMedia, File destFile) {
				System.out.println("Convert (" + count.incrementAndGet() + "/" + COUNT_TOTAL + "): " + srcMedia.getFile());
			}
		};

		try (ReduceImageConverter converter = new ReduceImageConverter(destFolder)) {
			converter.setSize(size);
			try (Classifier<ImageFinder, Image> classifier = ClassifierFactories.imagesToMovie().create(imageFinder, destFolder)) {
				return new PreparedImages(classifier.classify(converter, converterListener));
			}
		}
	}
}
