package org.fagu.fmv.im;

/*-
 * #%L
 * fmv-imagemagick
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import org.fagu.fmv.image.ImageMetadatas;
import org.fagu.fmv.image.ImageResourceUtils;
import org.fagu.fmv.image.TestAllImageMetadatasTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
class IMConvertImageMetadatasTestCase extends TestAllImageMetadatasTest {

	IMConvertImageMetadatasTestCase() {
		super(new IMConvertImageTestMetadataExtractor());
	}

	@Test
	void testMultiple() throws IOException {
		File file1 = ImageResourceUtils.extractFile(ImageResourceUtils.BAD_ASS_TOTTOO_FAIL);
		File file2 = ImageResourceUtils.extractFile(ImageResourceUtils.WEI_ASS);

		try {
			Map<File, IMConvertImageMetadatas> map = IMConvertImageMetadatas.with(Arrays.asList(file2, file1)).extractAll();
			assertMetadatas_WeiAss(map.get(file2));
			assertMetadatas_BadAssTottooFail(map.get(file1));
		} finally {
			if(file1 != null) {
				file1.delete();
			}
			if(file2 != null) {
				file2.delete();
			}
		}
	}

	@Test
	void testFile_animated_gif_animatedChecked() throws IOException {
		File file = ImageResourceUtils.extractFile(ImageResourceUtils.ANIMATED_GIF);

		try {
			IMConvertImageMetadatas metadatas = IMConvertImageMetadatas.with(file)
					.withAnimated(true)
					.extract();
			assertMetadatas_AnimatedGif(metadatas, true);
		} finally {
			if(file != null) {
				file.delete();
			}
		}
	}

	@Test
	@Disabled
	void testExtractSingleton() throws Exception {
		final File file = ImageResourceUtils.extractFile(ImageResourceUtils.PLAN4_550MPIXELS);
		try {
			Runnable runnable = new Runnable() {

				private int i;

				@Override
				public void run() {
					++i;
					System.out.println(i + " Run...");
					ImageMetadatas extract = IMConvertImageMetadatas.extractSingleton(file);
					System.out.println(extract);
					System.out.println(i + " Dimension: " + extract.getDimension());
					synchronized(this) {
						notifyAll();
					}
				}
			};
			Thread thread1 = new Thread(runnable);
			Thread thread2 = new Thread(runnable);
			Thread thread3 = new Thread(runnable);
			thread1.start();
			thread2.start();
			thread3.start();
			synchronized(this) {
				wait();
			}
		} finally {
			file.delete();
		}
	}

	// ********************************************

	private static final List<String> EXCLUDE_PROPERTIES = Arrays.asList(); // "ColorDepth", "CompressionQuality"

	private static final List<String> BAD_ASS_TOTTOO_FAIL_PROPERTIES = Arrays.asList("ResolutionUnit", "Software");

	private static final List<String> MULTIPAGE_TIFF_EXCLUDE_PROPERTIES = Arrays.asList("CompressionQuality", "ResolutionUnit", "Software");

	private static final List<String> PLAN4_550MPIXELS_EXCLUDE_PROPERTIES = Arrays.asList("CompressionQuality", "ResolutionUnit", "Software");

	private static final List<String> RABBITMQ_EXCLUDE_PROPERTIES = Arrays.asList("CompressionQuality", "Resolution", "ResolutionUnit");

	private static final List<String> WEI_ASS_EXCLUDE_PROPERTIES = Arrays.asList("Resolution", "ResolutionUnit");

	@Override
	protected BiPredicate<String, String> assertFilter() {
		return (fileName, property) -> {
			if(EXCLUDE_PROPERTIES.contains(property)) {
				return false;
			}

			if(ImageResourceUtils.BAD_ASS_TOTTOO_FAIL.equals(fileName) && BAD_ASS_TOTTOO_FAIL_PROPERTIES.contains(property)) {
				return false;
			}
			if(ImageResourceUtils.MULTIPAGE_TIFF.equals(fileName) && MULTIPAGE_TIFF_EXCLUDE_PROPERTIES.contains(property)) {
				return false;
			}
			if(ImageResourceUtils.PLAN4_550MPIXELS.equals(fileName) && PLAN4_550MPIXELS_EXCLUDE_PROPERTIES.contains(property)) {
				return false;
			}
			if(ImageResourceUtils.RABBITMQ.equals(fileName) && RABBITMQ_EXCLUDE_PROPERTIES.contains(property)) {
				return false;
			}
			if(ImageResourceUtils.WEI_ASS.equals(fileName) && WEI_ASS_EXCLUDE_PROPERTIES.contains(property)) {
				return false;
			}

			return true;
		};
	}

}
