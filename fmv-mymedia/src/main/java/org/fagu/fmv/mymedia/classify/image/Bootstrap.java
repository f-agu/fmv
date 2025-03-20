package org.fagu.fmv.mymedia.classify.image;

import java.awt.Color;

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
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.fagu.fmv.im.Image;
import org.fagu.fmv.im.soft.Convert;
import org.fagu.fmv.im.soft.Identify;
import org.fagu.fmv.mymedia.classify.Organizer;
import org.fagu.fmv.mymedia.classify.duplicate.AskDelete;
import org.fagu.fmv.mymedia.classify.duplicate.DeletePolicy;
import org.fagu.fmv.mymedia.classify.duplicate.DuplicateCleanPolicy;
import org.fagu.fmv.mymedia.classify.duplicate.DuplicatedFiles;
import org.fagu.fmv.mymedia.classify.duplicate.DuplicatedFiles.FileInfosFile;
import org.fagu.fmv.mymedia.classify.duplicate.DuplicatedResult;
import org.fagu.fmv.mymedia.classify.duplicate.KeepOlderDuplicateCleanPolicy;
import org.fagu.fmv.mymedia.file.ImageFinder;
import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.logger.LoggerFactory;
import org.fagu.fmv.mymedia.utils.ColorKMeans;
import org.fagu.fmv.soft.SoftLogger;
import org.fagu.fmv.textprogressbar.TextProgressBar;
import org.fagu.fmv.textprogressbar.part.SupplierTextPart;
import org.fagu.fmv.textprogressbar.part.TextPart;
import org.fagu.fmv.utils.file.FileFinderListener;
import org.fagu.fmv.utils.file.FindProgress;


/**
 * convert IMG_5291.JPG -auto-orient -quality 65% IMG_5291_2.JPG
 *
 * @author fagu
 */
public class Bootstrap {

	// ---------------------------------------------------

	private ImageFinder findImage(Logger logger, File saveFile, File... srcFiles) throws IOException {

		ImageFinder imagesFinder = new ImageFinder(logger, saveFile);
		imagesFinder.addListener(new FileFinderListener<Image>() {

		});
		try (FindProgress findProgress = new FindProgress() {

			private TextProgressBar textProgressBar;

			private SupplierTextPart supplierTextPart = new SupplierTextPart();

			@Override
			public void start() {
				textProgressBar = TextProgressBar.newBar()
						.append(new TextPart("Finding images: "))
						.append(supplierTextPart)
						.buildAndSchedule();
			}

			@Override
			public void progress(int done, int total) {
				if(total != 0) {
					supplierTextPart.setText(done + "/" + total + " (" + Integer.toString((100 * done) / total) + " %)                       ");
				} else {
					supplierTextPart.setText(done + "/" + total + "                                ");
				}
			}

			@Override
			public void progress(String text) {
				supplierTextPart.setText(text);
			}

			@Override
			public void close() throws IOException {
				if(textProgressBar != null) {
					textProgressBar.close();
				}
			}
		}) {
			imagesFinder.find(Arrays.asList(srcFiles), findProgress);
		}
		return imagesFinder;
	}

	@SuppressWarnings("unchecked")
	public static void main(String... args) throws IOException {
		checkSoft();
		System.out.println();
		File source = new File(args[0]);

		File saveFile = new File(source, "image.save");
		File destFolder = new File(source.getParentFile(), source.getName() + "-out");
		File deletedFolder = new File(source.getParentFile(), source.getName() + "-deleted");

		Bootstrap bootstrap = new Bootstrap();
		try (Logger logger = LoggerFactory.openLogger(LoggerFactory.getLogFile(source, "imagelog", "image.log"));
				ImageFinder imageFinder = bootstrap.findImage(logger, saveFile, source)) {

			logger.log("***************************************************");

			DuplicateCleanPolicy duplicateCleanPolicy = new KeepOlderDuplicateCleanPolicy(
					AskDelete.yesNoAlways(),
					DeletePolicy.moveTo(logger, deletedFolder),
					imageFinder);

			List<DuplicatedFiles<?>> duplicatedFilesList = List.of(
			// new BySizeDuplicatedFiles(logger),
			// new ByMD5DuplicatedFiles(logger),
			// new ByPerceptionHashDuplicatedFiles(logger, 0.01D, true)
			//
			);

			DuplicatedResult duplicatedResult = imageFinder.analyzeDuplicatedFiles(duplicatedFilesList);
			if(duplicatedResult.haveDuplicates()) {
				for(DuplicatedFiles<?> duplicatedFiles : duplicatedFilesList) {
					duplicateCleanPolicy.clean(duplicatedFiles, (Map<Object, List<FileInfosFile>>)duplicatedFiles.getDuplicateds());
				}
			}

			// colors
			analyzeColors(imageFinder, source, 10);

			Organizer<ImageFinder, Image> organizer = new Organizer<>(Image.class);
			organizer.organize(destFolder, imageFinder);
		}
	}

	// *******************************************************

	private static void checkSoft() {
		SoftLogger softLogger = SoftLogger.withSofts(List.of(
				Identify.search(),
				Convert.search()))
				.withColorization(true)
				.build();
		softLogger.log(System.out::println);
	}

	private static void analyzeColors(ImageFinder imageFinder, File folder, int numberOfCategory) {
		List<Color> trainingColors = new ArrayList<>();
		imageFinder.getAllMap()
				.forEach((fileFound, infosFile) -> infosFile
						.getInfo(Color.class)
						.ifPresent(trainingColors::add));

		ColorKMeans colorKMeans = new ColorKMeans(numberOfCategory, trainingColors);
		imageFinder.getAllMap()
				.forEach((fileFound, infosFile) -> {
					Color color = infosFile.getInfo(Color.class).orElseThrow();
					int category = colorKMeans.classify(color);
					File destFile = new File(folder, Integer.toString(category));
					if( ! destFile.exists()) {
						destFile.mkdirs();
					}
					destFile = new File(destFile, fileFound.getFileFound().getName());
					try {
						System.out.println(destFile);
						FileUtils.copyFile(fileFound.getFileFound(), destFile);
					} catch(IOException e) {
						e.printStackTrace();
					}
				});
	}

}
