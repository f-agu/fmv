package org.fagu.fmv.mymedia.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.TreeSet;

import org.fagu.fmv.im.IMIdentifyImageMetadatas;
import org.fagu.fmv.im.Image;
import org.fagu.fmv.mymedia.classify.image.TimeOffsetImageComparator;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.file.FileFinder.InfosFile;
import org.junit.jupiter.api.Test;

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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * @author f.agu
 */
class TimeOffsetImageComparatorTestCase {

	@Test
	void testEmpty() {
		TimeOffsetImageComparator comparator = new TimeOffsetImageComparator();
		FileFinder<Image>.InfosFile image1 = mockImage(1, "1");
		FileFinder<Image>.InfosFile image2 = mockImage(2, "2");
		FileFinder<Image>.InfosFile image1b = mockImage(1, "1b");
		assertEquals( - 1, comparator.compare(image1, image2));
		assertEquals(1, comparator.compare(image2, image1));
		assertEquals(1, comparator.compare(image1b, image1));
	}

	@Test
	void testSome() {
		TimeOffsetImageComparator comparator = new TimeOffsetImageComparator();
		comparator.addFilter(im -> "a".equals(im.getDevice()), 17);
		FileFinder<Image>.InfosFile imageA1 = mockImage(1, "a", "a", "A1"); // 18 = 1 + 17
		FileFinder<Image>.InfosFile imageA2 = mockImage(4, "a", "a", "A2"); // 21 = 4 + 17
		FileFinder<Image>.InfosFile imageB1 = mockImage(15, "b", "b", "B1"); // 12
		FileFinder<Image>.InfosFile imageB2 = mockImage(12, "b", "b", "B2"); // 15
		FileFinder<Image>.InfosFile imageB3 = mockImage(17, "b", "b", "B3"); // 17
		FileFinder<Image>.InfosFile imageB4 = mockImage(19, "b", "b", "B4"); // 19

		TreeSet<FileFinder<Image>.InfosFile> images = new TreeSet<>(comparator);
		images.add(imageA1);
		images.add(imageA2);
		images.add(imageB1);
		images.add(imageB2);
		images.add(imageB3);
		images.add(imageB4);

		Iterator<FileFinder<Image>.InfosFile> iterator = images.iterator();
		assertSame(imageB2, iterator.next());
		assertSame(imageB1, iterator.next());
		assertSame(imageB3, iterator.next());
		assertSame(imageA1, iterator.next());
		assertSame(imageB4, iterator.next());
		assertSame(imageA2, iterator.next());
		assertFalse(iterator.hasNext());
	}

	// **************************************

	private FileFinder<Image>.InfosFile mockImage(long time, String fileName) {
		return mockImage(time, null, null, fileName);
	}

	private FileFinder<Image>.InfosFile mockImage(long time, String device, String model, String fileName) {
		Image image = mock(Image.class);
		IMIdentifyImageMetadatas imageMetadatas = mock(IMIdentifyImageMetadatas.class);
		File file = mock(File.class);
		doReturn(time).when(image).getTime();
		doReturn(imageMetadatas).when(image).getMetadatas();
		OffsetDateTime expectedTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
		doReturn(expectedTime).when(imageMetadatas).getDate();
		if(device != null) {
			doReturn(device).when(imageMetadatas).getDevice();
		}
		if(model != null) {
			doReturn(model).when(imageMetadatas).getDeviceModel();
		}
		FileFinder<Image>.InfosFile infosFile = mock(InfosFile.class);
		doReturn(image).when(infosFile).getMain();
		doReturn(file).when(image).getFile();
		doReturn(fileName).when(file).getName();
		return infosFile;
	}
}
