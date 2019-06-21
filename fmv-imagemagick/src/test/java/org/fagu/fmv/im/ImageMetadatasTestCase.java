package org.fagu.fmv.im;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.fagu.fmv.utils.media.Size;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class ImageMetadatasTestCase {

	@Test
	public void testFile_bad_ass_tattoo() throws IOException {
		singleDoAndDelete("bad-ass-tattoo-fail.jpg", this::assertMetadatas_BadAssTottooFail);
	}

	@Test
	public void testFile_wei_ass() throws IOException {
		singleDoAndDelete("wei-ass.jpg", this::assertMetadatas_WeiAss);
	}

	@Test
	public void testFile_rabbitmq() throws IOException {
		singleDoAndDelete("rabbitmq.png", this::assertMetadatas_Rabbitmq);
	}

	@Test
	public void testFile_plan4() throws IOException {
		singleDoAndDelete("plan4-550Mpixels.tif", this::assertMetadatas_Plan4_550Mpixels);
	}

	@Test
	public void testFile_Multipage_tiff() throws IOException {
		singleDoAndDelete("multipage_tiff.tif", this::assertMetadatas_Multipage_tiff);
	}

	@Test
	public void testMultiple() throws IOException {
		File file1 = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File file2 = ImageResourceUtils.extractFile("wei-ass.jpg");

		try {
			Map<File, ImageMetadatas> map = ImageMetadatas.with(Arrays.asList(file2, file1)).extractAll();
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

	@Test
	public void testFailed() throws Exception {
		File file = ImageResourceUtils.extractFile("no-image", "jpg");
		try {
			ImageMetadatas.with(file).extract();
			fail();
		} catch(IOException e) {
			String message = e.getMessage();
			assertTrue(message.contains("Not a JPEG file") || message.contains("insufficient image data"));
		} finally {
			if(file != null) {
				file.delete();
			}
		}
	}

	@Test
	@Ignore
	public void testExtractSingleton() throws Exception {
		final File file = ImageResourceUtils.extractFile("plan4-550Mpixels.tif");
		try {
			Runnable runnable = new Runnable() {

				private int i;

				@Override
				public void run() {
					++i;
					System.out.println(i + " Run...");
					ImageMetadatas extract = ImageMetadatas.extractSingleton(file);
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

	private void singleDoAndDelete(String resourceName, Consumer<ImageMetadatas> assertConsumer) throws IOException {
		// by file
		File file = ImageResourceUtils.extractFile(resourceName);
		try {
			ImageMetadatas metadatas = ImageMetadatas.with(file).extract();
			assertConsumer.accept(metadatas);
		} finally {
			if(file != null && ! file.delete()) {
				fail("Unable to delete " + file);
			}
		}

		// by inputStream
		try (InputStream inputStream = getClass().getResourceAsStream(resourceName)) {
			ImageMetadatas metadatas = ImageMetadatas.with(inputStream).extract();
			assertConsumer.accept(metadatas);
		}
	}

	/**
	 * bad-ass-tattoo-fail.jpg
	 * 
	 * @param metadatas
	 */
	private void assertMetadatas_BadAssTottooFail(ImageMetadatas metadatas) {
		assertNull("Aperture", metadatas.getAperture());
		assertEquals("ApertureFormat", "", metadatas.getApertureFormat());
		assertEquals("ColorDepth", 8, metadatas.getColorDepth());
		assertEquals("ColorSpace", "sRGB", metadatas.getColorSpace());
		assertEquals("Compression", "JPEG", metadatas.getCompression());
		assertEquals("CompressionQuality", 91, metadatas.getCompressionQuality());
		assertNull("Coordinates", metadatas.getCoordinates());
		assertNotNull("Date", metadatas.getDate());
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
		assertEquals("ColorDepth", 8, metadatas.getColorDepth());
		assertEquals("ColorSpace", "sRGB", metadatas.getColorSpace());
		assertEquals("Compression", "JPEG", metadatas.getCompression());
		assertEquals("CompressionQuality", 74, metadatas.getCompressionQuality());
		assertNull("Coordinates", metadatas.getCoordinates());
		assertNotNull("Date", metadatas.getDate());
		assertNull("Device", metadatas.getDevice());
		assertNull("DeviceModel", metadatas.getDeviceModel());
		assertEquals("Dimension", Size.valueOf(1024, 768), metadatas.getDimension());
		assertNull("ExposureTime", metadatas.getExposureTime());
		assertEquals("ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		assertNull("Flash", metadatas.getFlash());
		assertNull("FocalLength", metadatas.getFlash());
		assertNull("ISOSpeed", metadatas.getISOSpeed());
		assertEquals("Resolution", Size.valueOf(72, 72), metadatas.getResolution());
		assertEquals("ResolutionUnit", "Undefined", metadatas.getResolutionUnit());
		assertNull("Software", metadatas.getSoftware());

		assertEquals("jpeg:colorspace", "2", metadatas.get("jpeg:colorspace"));
		assertEquals("jpeg:sampling-factor", "2x1,1x1,1x1", metadatas.get("jpeg:sampling-factor"));
	}

	/**
	 * rabbitmq.png
	 * 
	 * @param metadatas
	 */
	private void assertMetadatas_Rabbitmq(ImageMetadatas metadatas) {
		assertNull("Aperture", metadatas.getAperture());
		assertEquals("ApertureFormat", "", metadatas.getApertureFormat());
		assertEquals("ColorDepth", 8, metadatas.getColorDepth());
		assertEquals("ColorSpace", "sRGB", metadatas.getColorSpace());
		assertEquals("Compression", "Zip", metadatas.getCompression());
		assertEquals("CompressionQuality", 92, metadatas.getCompressionQuality());
		assertNull("Coordinates", metadatas.getCoordinates());
		assertNotNull("Date", metadatas.getDate());
		assertNull("Device", metadatas.getDevice());
		assertNull("DeviceModel", metadatas.getDeviceModel());
		assertEquals("Dimension", Size.valueOf(2362, 2500), metadatas.getDimension());
		assertNull("ExposureTime", metadatas.getExposureTime());
		assertEquals("ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		assertNull("Flash", metadatas.getFlash());
		assertNull("FocalLength", metadatas.getFlash());
		assertNull("ISOSpeed", metadatas.getISOSpeed());
		assertEquals("Resolution", Size.valueOf(72, 72), metadatas.getResolution());
		assertEquals("ResolutionUnit", "Undefined", metadatas.getResolutionUnit());
		assertNull("Software", metadatas.getSoftware());

		assertEquals("png:chrm", "chunk was found (see Chromaticity, above)", metadatas.get("png:chrm"));
		assertEquals("png:gama", "gamma=0.45455 (See Gamma, above)", metadatas.get("png:gama"));
		assertEquals("png:ihdr.bit-depth-orig", "8", metadatas.get("png:ihdr.bit-depth-orig"));
		assertEquals("png:ihdr.bit_depth", "8", metadatas.get("png:ihdr.bit_depth"));
		assertEquals("png:ihdr.color-type-orig", "6", metadatas.get("png:ihdr.color-type-orig"));
		assertEquals("png:ihdr.color_type", "6 (RGBA)", metadatas.get("png:ihdr.color_type"));
		assertEquals("png:ihdr.interlace_method", "0 (Not interlaced)", metadatas.get("png:ihdr.interlace_method"));
		assertEquals("png:ihdr.width,height", "2362, 2500", metadatas.get("png:ihdr.width,height"));
		assertEquals("png:srgb", "intent=0 (Perceptual Intent)", metadatas.get("png:srgb"));
	}

	/**
	 * plan4-550Mpixels.tif
	 * 
	 * @param metadatas
	 */
	private void assertMetadatas_Plan4_550Mpixels(ImageMetadatas metadatas) {
		assertNull("Aperture", metadatas.getAperture());
		assertEquals("ApertureFormat", "", metadatas.getApertureFormat());
		assertEquals("ColorDepth", 1, metadatas.getColorDepth());
		assertEquals("ColorSpace", "Gray", metadatas.getColorSpace());
		assertEquals("Compression", "Group4", metadatas.getCompression());
		assertEquals("CompressionQuality", 92, metadatas.getCompressionQuality());
		assertNull("Coordinates", metadatas.getCoordinates());
		assertNotNull("Date", metadatas.getDate());
		assertNull("Device", metadatas.getDevice());
		assertNull("DeviceModel", metadatas.getDeviceModel());
		assertEquals("Dimension", Size.valueOf(28083, 19867), metadatas.getDimension());
		assertNull("ExposureTime", metadatas.getExposureTime());
		assertEquals("ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		assertNull("Flash", metadatas.getFlash());
		assertNull("FocalLength", metadatas.getFlash());
		assertNull("ISOSpeed", metadatas.getISOSpeed());
		assertEquals("Resolution", Size.valueOf(600, 600), metadatas.getResolution());
		assertEquals("ResolutionUnit", "PixelsPerInch", metadatas.getResolutionUnit());
		assertNull("Software", metadatas.getSoftware());

		assertEquals("tiff:alpha", "unspecified", metadatas.get("tiff:alpha"));
		assertEquals("tiff:endian", "lsb", metadatas.get("tiff:endian"));
		assertEquals("tiff:photometric", "min-is-white", metadatas.get("tiff:photometric"));
		assertEquals("tiff:rows-per-strip", "19867", metadatas.get("tiff:rows-per-strip"));
		assertEquals("tiff:software", "GPL Ghostscript 8.54", metadatas.get("tiff:software"));
		assertEquals("tiff:subfiletype", "PAGE", metadatas.get("tiff:subfiletype"));
		assertEquals("tiff:timestamp", "2011:09:21 08:05:12", metadatas.get("tiff:timestamp"));
	}

	/**
	 * multipage_tiff.tif
	 * 
	 * @param metadatas
	 */
	private void assertMetadatas_Multipage_tiff(ImageMetadatas metadatas) {
		assertNull("Aperture", metadatas.getAperture());
		assertEquals("ApertureFormat", "", metadatas.getApertureFormat());
		assertEquals("ColorDepth", 8, metadatas.getColorDepth());
		assertEquals("ColorSpace", "sRGB", metadatas.getColorSpace());
		assertEquals("Compression", "JPEG", metadatas.getCompression());
		assertEquals("CompressionQuality", 92, metadatas.getCompressionQuality());
		assertNull("Coordinates", metadatas.getCoordinates());
		assertNotNull("Date", metadatas.getDate());
		assertNull("Device", metadatas.getDevice());
		assertNull("DeviceModel", metadatas.getDeviceModel());
		assertEquals("Dimension", Size.valueOf(800, 600), metadatas.getDimension());
		assertNull("ExposureTime", metadatas.getExposureTime());
		assertEquals("ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		assertNull("Flash", metadatas.getFlash());
		assertNull("FocalLength", metadatas.getFlash());
		assertNull("ISOSpeed", metadatas.getISOSpeed());
		assertEquals("Resolution", Size.valueOf(96, 96), metadatas.getResolution());
		assertEquals("ResolutionUnit", "PixelsPerInch", metadatas.getResolutionUnit());
		assertNull("Software", metadatas.getSoftware());

		assertEquals("tiff:alpha", "unspecified", metadatas.get("tiff:alpha"));
		assertEquals("tiff:endian", "lsb", metadatas.get("tiff:endian"));
		assertEquals("tiff:photometric", "RGB", metadatas.get("tiff:photometric"));
		assertEquals("tiff:rows-per-strip", "16", metadatas.get("tiff:rows-per-strip"));
		assertEquals("tiff:software", "IrfanView", metadatas.get("tiff:software"));
	}

}
