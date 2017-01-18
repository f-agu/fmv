package org.fagu.fmv.image;

/*
 * #%L
 * fmv-image
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.fagu.fmv.utils.media.Size;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class ImageMetadatasTestCase {

	/**
	 *
	 */
	public ImageMetadatasTestCase() {}

	/**
	 * @throws Exception
	 */
	@Test
	public void testSimple1() throws Exception {
		File file = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		try {
			ImageMetadatas metadatas = ImageMetadatas.extract(file);
			assertMetadatas_BadAssTottooFail(metadatas);
		} finally {
			if(file != null) {
				file.delete();
			}
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testSimple2() throws Exception {
		File file = ImageResourceUtils.extractFile("wei-ass.jpg");
		try {
			ImageMetadatas metadatas = ImageMetadatas.extract(file);
			assertMetadatas_WeiAss(metadatas);
		} finally {
			if(file != null) {
				file.delete();
			}
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testMultiple() throws Exception {
		File file1 = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File file2 = ImageResourceUtils.extractFile("wei-ass.jpg");

		try {
			Map<File, ImageMetadatas> map = ImageMetadatas.extract(Arrays.asList(file2, file1));
			Iterator<ImageMetadatas> iterator = map.values().iterator();
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

	/**
	 * @throws Exception
	 */
	@Test
	public void testFailed() throws Exception {
		File file = ImageResourceUtils.extractFile("no-image", "jpg");
		try {
			ImageMetadatas.extract(file);
			fail();
		} catch(IOException e) {
			assertTrue(e.getMessage().contains("Not a JPEG file"));
		} finally {
			if(file != null) {
				file.delete();
			}
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testExtractSingleton() throws Exception {
		final File file = ImageResourceUtils.extractFile("plan4-550Mpixels.tif");
		try {
			Runnable runnable = new Runnable() {

				private int i;

				/**
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					++i;
					System.out.println(i + " Run...");
					try {
						ImageMetadatas extract = ImageMetadatas.extractSingleton(file);
						System.out.println(extract);
						System.out.println(i + " Dimension: " + extract.getDimension());
					} catch(IOException e) {
						throw new RuntimeException(e);
					}
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

	/**
	 * bad-ass-tattoo-fail.jpg
	 * 
	 * @param metadatas
	 */
	private void assertMetadatas_BadAssTottooFail(ImageMetadatas metadatas) {
		assertNull("Aperture", metadatas.getAperture());
		assertEquals("ApertureFormat", "", metadatas.getApertureFormat());
		assertEquals("ColorSpace", "sRGB", metadatas.getColorSpace());
		assertNull("Coordinates", metadatas.getCoordinates());
		assertNull("Date", metadatas.getDate());
		assertNull("Device", metadatas.getDevice());
		assertNull("DeviceModel", metadatas.getDeviceModel());
		assertEquals("Dimension", Size.valueOf(450, 428), metadatas.getDimension());
		assertNull("ExposureTime", metadatas.getExposureTime());
		assertEquals("ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		assertNull("Flash", metadatas.getFlash());
		assertNull("FocalLength", metadatas.getFlash());
		assertNull("ISOSpeed", metadatas.getISOSpeed());
		assertEquals("Resolution", Size.valueOf(72, 72), metadatas.getResolution());
		assertEquals("Software", "Adobe Photoshop CS3 Windows", metadatas.getSoftware());
	}

	/**
	 * wei-ass.jpg
	 * 
	 * @param metadatas
	 */
	private void assertMetadatas_WeiAss(ImageMetadatas metadatas) {
		assertNull("Aperture", metadatas.getAperture());
		assertEquals("ApertureFormat", "", metadatas.getApertureFormat());
		assertEquals("ColorSpace", "sRGB", metadatas.getColorSpace());
		assertNull("Coordinates", metadatas.getCoordinates());
		assertNull("Date", metadatas.getDate());
		assertNull("Device", metadatas.getDevice());
		assertNull("DeviceModel", metadatas.getDeviceModel());
		assertEquals("Dimension", Size.valueOf(1024, 768), metadatas.getDimension());
		assertNull("ExposureTime", metadatas.getExposureTime());
		assertEquals("ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		assertNull("Flash", metadatas.getFlash());
		assertNull("FocalLength", metadatas.getFlash());
		assertNull("ISOSpeed", metadatas.getISOSpeed());
		assertEquals("Resolution", Size.valueOf(72, 72), metadatas.getResolution());
		assertNull("Software", metadatas.getSoftware());
	}

}
