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
import java.util.Iterator;
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
class IMIdentifyImageMetadatasTestCase extends TestAllImageMetadatasTest {

	public IMIdentifyImageMetadatasTestCase() {
		super(new IMIdentifyImageTestMetadataExtractor());
	}

	@Test
	void testMultiple() throws IOException {
		File file1 = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File file2 = ImageResourceUtils.extractFile("wei-ass.jpg");

		try {
			Map<File, IMIdentifyImageMetadatas> map = IMIdentifyImageMetadatas.with(Arrays.asList(file2, file1)).extractAll();
			Iterator<IMIdentifyImageMetadatas> iterator = map.values().iterator();
			assertMetadatas_WeiAss(iterator.next());
			assertMetadatas_BadAssTottooFail(iterator.next());
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
	@Disabled
	void testExtractSingleton() throws Exception {
		final File file = ImageResourceUtils.extractFile("plan4-550Mpixels.tif");
		try {
			Runnable runnable = new Runnable() {

				private int i;

				@Override
				public void run() {
					++i;
					System.out.println(i + " Run...");
					ImageMetadatas extract = IMIdentifyImageMetadatas.extractSingleton(file);
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

	private static final List<String> MULTIPAGE_TIFF_EXCLUDE_PROPERTIES = Arrays.asList("Software");

	private static final List<String> PLAN4_550MPIXELS_EXCLUDE_PROPERTIES = Arrays.asList("Software");

	@Override
	protected BiPredicate<String, String> assertFilter() {
		return (fileName, property) -> {
			if(ImageResourceUtils.MULTIPAGE_TIFF.equals(fileName) && MULTIPAGE_TIFF_EXCLUDE_PROPERTIES.contains(property)) {
				return false;
			}
			if(ImageResourceUtils.PLAN4_550MPIXELS.equals(fileName) && PLAN4_550MPIXELS_EXCLUDE_PROPERTIES.contains(property)) {
				return false;
			}

			return true;
		};
	}

}
