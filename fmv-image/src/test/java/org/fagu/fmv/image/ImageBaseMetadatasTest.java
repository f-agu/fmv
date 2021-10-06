package org.fagu.fmv.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import org.apache.commons.collections4.CollectionUtils;
import org.fagu.fmv.media.BaseMetadatasTest;
import org.fagu.fmv.media.TestMetadataExtractor;
import org.fagu.fmv.utils.media.Size;
import org.junit.jupiter.api.AfterAll;


/**
 * @author f.agu
 */
class ImageBaseMetadatasTest extends BaseMetadatasTest<ImageMetadatas> {

	private static final Map<String, Set<String>> FAILED_KEYS = new HashMap<>();

	protected static boolean DEBUG = false;

	public ImageBaseMetadatasTest(TestMetadataExtractor<ImageMetadatas> testMetadataExtractor) {
		super(
				testMetadataExtractor,
				ImageResourceUtils::extractFile,
				ImageBaseMetadatasTest.class::getResourceAsStream);
	}

	@AfterAll
	public static void debugFailedKeys() {
		if(DEBUG) {
			System.out.println();
			System.out.println("Global keys to exclude :");
			FAILED_KEYS.values().stream()
					.reduce((a, b) -> new HashSet<>(CollectionUtils.intersection(a, b)))
					.orElse(Collections.emptySet())
					.stream()
					.sorted()
					.forEach(System.out::println);
		}
	}

	/**
	 * 203.jpg
	 * 
	 * @param metadatas
	 */
	public void assertMetadatas_203(ImageMetadatas metadatas) {
		// display(metadatas);
		final String fileName = ImageResourceUtils._203;
		mdAssertEquals(fileName, "Aperture", Float.valueOf(2.8F), metadatas.getAperture(), 0.1);
		mdAssertEquals(fileName, "ApertureFormat", "F/2.8", metadatas.getApertureFormat());
		mdAssertEquals(fileName, "ColorDepth", Integer.valueOf(8), metadatas.getColorDepth());
		mdAssertEquals(fileName, "ColorSpace", "sRGB", metadatas.getColorSpace());
		mdAssertEquals(fileName, "Compression", "JPEG", metadatas.getCompression());
		mdAssertEquals(fileName, "CompressionQuality", Integer.valueOf(65), metadatas.getCompressionQuality());
		mdAssertNull(fileName, "Coordinates", metadatas.getCoordinates());
		mdAssertEquals(fileName, "Date", "2019-08-16T11:20:23+02:00", metadatas.getDate().toString());
		mdAssertEquals(fileName, "Device", "Canon", metadatas.getDevice());
		mdAssertEquals(fileName, "DeviceModel", "Canon EOS 500D", metadatas.getDeviceModel());
		mdAssertEquals(fileName, "Dimension", Size.valueOf(4752, 3168), metadatas.getDimension());
		mdAssertEquals(fileName, "ExposureTime", Float.valueOf(0.02F), metadatas.getExposureTime());
		mdAssertEquals(fileName, "ExposureTimeFormat", "1/50", metadatas.getExposureTimeFormat());
		mdAssertEquals(fileName, "Flash", "Flash(16) Off, Did not fire", String.valueOf(metadatas.getFlash()));
		mdAssertEquals(fileName, "FocalLength", Float.valueOf(50F), metadatas.getFocalLength());
		mdAssertEquals(fileName, "Format", "JPEG", String.valueOf(metadatas.getFormat()).toUpperCase());
		mdAssertEquals(fileName, "ISO", Integer.valueOf(100), metadatas.getISOSpeed());
		mdAssertEquals(fileName, "Resolution", Size.valueOf(72, 72), metadatas.getResolution());
		mdAssertNull(fileName, "Software", metadatas.getSoftware());
	}

	/**
	 * 104.jpg
	 * 
	 * @param metadatas
	 */
	public void assertMetadatas_104(ImageMetadatas metadatas) {
		// display(metadatas);
		final String fileName = ImageResourceUtils._104;
		mdAssertEquals(fileName, "Aperture", Float.valueOf(2F), metadatas.getAperture());
		mdAssertEquals(fileName, "ApertureFormat", "F/2.0", metadatas.getApertureFormat());
		mdAssertEquals(fileName, "ColorDepth", Integer.valueOf(8), metadatas.getColorDepth());
		mdAssertEquals(fileName, "ColorSpace", "sRGB", metadatas.getColorSpace());
		mdAssertEquals(fileName, "Compression", "JPEG", metadatas.getCompression());
		mdAssertEquals(fileName, "CompressionQuality", Integer.valueOf(65), metadatas.getCompressionQuality());
		mdAssertEquals(fileName, "Coordinates", "45° 55' 20.35\" N, 6° 52' 7.11\" E", String.valueOf(metadatas.getCoordinates()));
		mdAssertEquals(fileName, "Date", "2019-08-14T10:20:58+02:00", metadatas.getDate().toString());
		mdAssertEquals(fileName, "Device", "LGE", metadatas.getDevice());
		mdAssertEquals(fileName, "DeviceModel", "Nexus 5X", metadatas.getDeviceModel());
		mdAssertEquals(fileName, "Dimension", Size.valueOf(4032, 3024), metadatas.getDimension());
		mdAssertEquals(fileName, "ExposureTime", Float.valueOf(0.001046721F), metadatas.getExposureTime());
		mdAssertEquals(fileName, "ExposureTimeFormat", "1/955", metadatas.getExposureTimeFormat());
		mdAssertEquals(fileName, "Flash", "Flash(0) No Flash", String.valueOf(metadatas.getFlash()));
		mdAssertEquals(fileName, "FocalLength", Float.valueOf(4.67F), metadatas.getFocalLength());
		mdAssertEquals(fileName, "Format", "JPEG", String.valueOf(metadatas.getFormat()).toUpperCase());
		mdAssertEquals(fileName, "ISO", Integer.valueOf(60), metadatas.getISOSpeed());
		mdAssertEquals(fileName, "Resolution", Size.valueOf(72, 72), metadatas.getResolution());
		mdAssertEquals(fileName, "Software", "bullhead-user 8.1.0 OPM7.181205.001 5080180 release-keys", metadatas.getSoftware());
	}

	/**
	 * bad-ass-tattoo-fail.jpg
	 * 
	 * @param metadatas assertMetadatas_104
	 */
	public void assertMetadatas_BadAssTottooFail(ImageMetadatas metadatas) {
		// display(metadatas);
		final String fileName = ImageResourceUtils.BAD_ASS_TOTTOO_FAIL;
		mdAssertNull(fileName, "Aperture", metadatas.getAperture());
		mdAssertEquals(fileName, "ApertureFormat", "", metadatas.getApertureFormat());
		mdAssertEquals(fileName, "ColorDepth", Integer.valueOf(8), metadatas.getColorDepth());
		mdAssertEquals(fileName, "ColorSpace", "sRGB", metadatas.getColorSpace());
		mdAssertEquals(fileName, "Compression", "JPEG", metadatas.getCompression());
		mdAssertEquals(fileName, "CompressionQuality", Integer.valueOf(91), metadatas.getCompressionQuality());
		mdAssertNull(fileName, "Coordinates", metadatas.getCoordinates());
		mdAssertEquals(fileName, "Date", "2010-01-02T20:03:43+01:00", metadatas.getDate().toString());
		mdAssertNull(fileName, "Device", metadatas.getDevice());
		mdAssertNull(fileName, "DeviceModel", metadatas.getDeviceModel());
		mdAssertEquals(fileName, "Dimension", Size.valueOf(450, 428), metadatas.getDimension());
		mdAssertNull(fileName, "ExposureTime", metadatas.getExposureTime());
		mdAssertEquals(fileName, "ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		mdAssertNull(fileName, "Flash", metadatas.getFlash());
		mdAssertNull(fileName, "FocalLength", metadatas.getFocalLength());
		mdAssertEquals(fileName, "Format", "JPEG", String.valueOf(metadatas.getFormat()).toUpperCase());
		mdAssertNull(fileName, "ISO", metadatas.getISOSpeed());
		mdAssertEquals(fileName, "Resolution", Size.valueOf(72, 72), metadatas.getResolution());
		mdAssertEquals(fileName, "Software", "Adobe Photoshop CS3 Windows", metadatas.getSoftware());
	}

	/**
	 * wei-ass.jpg
	 * 
	 * @param metadatas
	 */
	public void assertMetadatas_WeiAss(ImageMetadatas metadatas) {
		// display(metadatas);
		final String fileName = ImageResourceUtils.WEI_ASS;
		mdAssertNull(fileName, "Aperture", metadatas.getAperture());
		mdAssertEquals(fileName, "ApertureFormat", "", metadatas.getApertureFormat());
		mdAssertEquals(fileName, "ColorDepth", Integer.valueOf(8), metadatas.getColorDepth());
		mdAssertEquals(fileName, "ColorSpace", "sRGB", metadatas.getColorSpace());
		mdAssertEquals(fileName, "Compression", "JPEG", metadatas.getCompression());
		mdAssertEquals(fileName, "CompressionQuality", Integer.valueOf(74), metadatas.getCompressionQuality());
		mdAssertNull(fileName, "Coordinates", metadatas.getCoordinates());
		mdAssertNotNull(fileName, "Date", metadatas.getDate()); // no date defined in metadatas
		mdAssertNull(fileName, "Device", metadatas.getDevice());
		mdAssertNull(fileName, "DeviceModel", metadatas.getDeviceModel());
		mdAssertEquals(fileName, "Dimension", Size.valueOf(1024, 768), metadatas.getDimension());
		mdAssertNull(fileName, "ExposureTime", metadatas.getExposureTime());
		mdAssertEquals(fileName, "ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		mdAssertNull(fileName, "Flash", metadatas.getFlash());
		mdAssertNull(fileName, "FocalLength", metadatas.getFocalLength());
		mdAssertEquals(fileName, "Format", "JPEG", String.valueOf(metadatas.getFormat()).toUpperCase());
		mdAssertNull(fileName, "ISO", metadatas.getISOSpeed());
		mdAssertEquals(fileName, "Resolution", Size.valueOf(72, 72), metadatas.getResolution());
		mdAssertNull(fileName, "ResolutionUnit", metadatas.getResolutionUnit());
		mdAssertNull(fileName, "Software", metadatas.getSoftware());

		mdAssertEquals(fileName, "jpeg:colorspace", "2", metadatas.get("jpeg:colorspace"));
		mdAssertEquals(fileName, "jpeg:sampling-factor", "2x1,1x1,1x1", metadatas.get("jpeg:sampling-factor"));
	}

	/**
	 * rabbitmq.png
	 * 
	 * @param metadatas
	 */
	public void assertMetadatas_Rabbitmq(ImageMetadatas metadatas) {
		// display(metadatas);
		final String fileName = ImageResourceUtils.RABBITMQ;
		mdAssertNull(fileName, "Aperture", metadatas.getAperture());
		mdAssertEquals(fileName, "ApertureFormat", "", metadatas.getApertureFormat());
		mdAssertEquals(fileName, "ColorDepth", Integer.valueOf(8), metadatas.getColorDepth());
		mdAssertEquals(fileName, "ColorSpace", "sRGB", metadatas.getColorSpace());
		mdAssertEquals(fileName, "Compression", "Zip", metadatas.getCompression());
		mdAssertEquals(fileName, "CompressionQuality", Integer.valueOf(92), metadatas.getCompressionQuality());
		mdAssertNull(fileName, "Coordinates", metadatas.getCoordinates());
		mdAssertNotNull(fileName, "Date", metadatas.getDate()); // no date defined in metadatas
		mdAssertNull(fileName, "Device", metadatas.getDevice());
		mdAssertNull(fileName, "DeviceModel", metadatas.getDeviceModel());
		mdAssertEquals(fileName, "Dimension", Size.valueOf(2362, 2500), metadatas.getDimension());
		mdAssertNull(fileName, "ExposureTime", metadatas.getExposureTime());
		mdAssertEquals(fileName, "ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		mdAssertNull(fileName, "Flash", metadatas.getFlash());
		mdAssertNull(fileName, "FocalLength", metadatas.getFocalLength());
		mdAssertEquals(fileName, "Format", "PNG", String.valueOf(metadatas.getFormat()).toUpperCase());
		mdAssertNull(fileName, "ISO", metadatas.getISOSpeed());
		mdAssertEquals(fileName, "Resolution", Size.valueOf(72, 72), metadatas.getResolution());
		mdAssertNull(fileName, "ResolutionUnit", metadatas.getResolutionUnit());
		mdAssertNull(fileName, "Software", metadatas.getSoftware());

		// mdAssertEquals(fileName, "png:chrm", "chunk was found (see Chromaticity, above)", metadatas.get("png:chrm"));
		// mdAssertEquals(fileName, "png:gama", "gamma=0.45455 (See Gamma, above)", metadatas.get("png:gama"));
		mdAssertEquals(fileName, "png:ihdr.bit-depth-orig", "8", metadatas.get("png:ihdr.bit-depth-orig"));
		mdAssertEquals(fileName, "png:ihdr.bit_depth", "8", metadatas.get("png:ihdr.bit_depth"));
		mdAssertEquals(fileName, "png:ihdr.color-type-orig", "6", metadatas.get("png:ihdr.color-type-orig"));
		mdAssertEquals(fileName, "png:ihdr.color_type", "6 (RGBA)", metadatas.get("png:ihdr.color_type"));
		mdAssertEquals(fileName, "png:ihdr.interlace_method", "0 (Not interlaced)", metadatas.get("png:ihdr.interlace_method"));
		mdAssertEquals(fileName, "png:ihdr.width,height", "2362, 2500", metadatas.get("png:ihdr.width,height"));
		// mdAssertEquals(fileName, "png:srgb", "intent=0 (Perceptual Intent)", metadatas.get("png:srgb"));
	}

	/**
	 * plan4-550Mpixels.tif
	 * 
	 * @param metadatas
	 */
	public void assertMetadatas_Plan4_550Mpixels(ImageMetadatas metadatas) {
		// display(metadatas);
		final String fileName = ImageResourceUtils.PLAN4_550MPIXELS;
		mdAssertNull(fileName, "Aperture", metadatas.getAperture());
		mdAssertEquals(fileName, "ApertureFormat", "", metadatas.getApertureFormat());
		mdAssertEquals(fileName, "ColorDepth", Integer.valueOf(1), metadatas.getColorDepth());
		mdAssertEquals(fileName, "ColorSpace", "Gray", metadatas.getColorSpace());
		mdAssertEquals(fileName, "Compression", "Group4", metadatas.getCompression());
		mdAssertEquals(fileName, "CompressionQuality", Integer.valueOf(92), metadatas.getCompressionQuality());
		mdAssertNull(fileName, "Coordinates", metadatas.getCoordinates());
		mdAssertNotNull(fileName, "Date", metadatas.getDate()); // no date defined in metadatas
		mdAssertNull(fileName, "Device", metadatas.getDevice());
		mdAssertNull(fileName, "DeviceModel", metadatas.getDeviceModel());
		mdAssertEquals(fileName, "Dimension", Size.valueOf(28083, 19867), metadatas.getDimension());
		mdAssertNull(fileName, "ExposureTime", metadatas.getExposureTime());
		mdAssertEquals(fileName, "ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		mdAssertNull(fileName, "Flash", metadatas.getFlash());
		mdAssertNull(fileName, "FocalLength", metadatas.getFocalLength());
		mdAssertEquals(fileName, "Format", "TIFF", String.valueOf(metadatas.getFormat()).toUpperCase());
		mdAssertNull(fileName, "ISO", metadatas.getISOSpeed());
		mdAssertEquals(fileName, "Resolution", Size.valueOf(600, 600), metadatas.getResolution());
		mdAssertEquals(fileName, "ResolutionUnit", "PixelsPerInch", metadatas.getResolutionUnit());
		mdAssertEquals(fileName, "Software", "GPL Ghostscript 8.54", metadatas.getSoftware());

		// mdAssertEquals(fileName, "tiff:alpha", "unspecified", metadatas.get("tiff:alpha"));
		mdAssertEquals(fileName, "tiff:endian", "lsb", metadatas.get("tiff:endian"));
		mdAssertEquals(fileName, "tiff:photometric", "min-is-white", metadatas.get("tiff:photometric"));
		// mdAssertEquals(fileName, "tiff:rows-per-strip", "19867", metadatas.get("tiff:rows-per-strip"));
		mdAssertEquals(fileName, "tiff:software", "GPL Ghostscript 8.54", metadatas.get("tiff:software"));
		mdAssertEquals(fileName, "tiff:subfiletype", "PAGE", metadatas.get("tiff:subfiletype"));
		mdAssertEquals(fileName, "tiff:timestamp", "2011:09:21 08:05:12", metadatas.get("tiff:timestamp"));
	}

	/**
	 * multipage_tiff.tif
	 * 
	 * @param metadatas
	 */
	public void assertMetadatas_Multipage_tiff(ImageMetadatas metadatas) {
		// display(metadatas);
		final String fileName = ImageResourceUtils.MULTIPAGE_TIFF;
		mdAssertNull(fileName, "Aperture", metadatas.getAperture());
		mdAssertEquals(fileName, "ApertureFormat", "", metadatas.getApertureFormat());
		mdAssertEquals(fileName, "ColorDepth", Integer.valueOf(8), metadatas.getColorDepth());
		mdAssertEquals(fileName, "ColorSpace", "sRGB", metadatas.getColorSpace());
		mdAssertEquals(fileName, "Compression", "JPEG", metadatas.getCompression());
		mdAssertEquals(fileName, "CompressionQuality", Integer.valueOf(92), metadatas.getCompressionQuality());
		mdAssertNull(fileName, "Coordinates", metadatas.getCoordinates());
		mdAssertNotNull(fileName, "Date", metadatas.getDate()); // no date defined in metadatas
		mdAssertNull(fileName, "Device", metadatas.getDevice());
		mdAssertNull(fileName, "DeviceModel", metadatas.getDeviceModel());
		mdAssertEquals(fileName, "Dimension", Size.valueOf(800, 600), metadatas.getDimension());
		mdAssertNull(fileName, "ExposureTime", metadatas.getExposureTime());
		mdAssertEquals(fileName, "ExposureTimeFormat", "", metadatas.getExposureTimeFormat());
		mdAssertNull(fileName, "Flash", metadatas.getFlash());
		mdAssertNull(fileName, "FocalLength", metadatas.getFocalLength());
		mdAssertEquals(fileName, "Format", "TIFF", String.valueOf(metadatas.getFormat()).toUpperCase());
		mdAssertNull(fileName, "ISO", metadatas.getISOSpeed());
		mdAssertEquals(fileName, "Resolution", Size.valueOf(96, 96), metadatas.getResolution());
		mdAssertEquals(fileName, "ResolutionUnit", "PixelsPerInch", metadatas.getResolutionUnit());
		mdAssertEquals(fileName, "Software", "IrfanView", metadatas.getSoftware());

		// mdAssertEquals(fileName, "tiff:alpha", "unspecified", metadatas.get("tiff:alpha"));
		mdAssertEquals(fileName, "tiff:endian", "lsb", metadatas.get("tiff:endian"));
		mdAssertEquals(fileName, "tiff:photometric", "RGB", metadatas.get("tiff:photometric"));
		// mdAssertEquals(fileName, "tiff:rows-per-strip", "16", metadatas.get("tiff:rows-per-strip"));
		mdAssertEquals(fileName, "tiff:software", "IrfanView", metadatas.get("tiff:software"));
	}

	public void display(ImageMetadatas metadatas) {
		metadatas.getData().forEach((k, v) -> System.out.println(k + " : " + v));
	}

	// *****************************************

	protected BiPredicate<String, String> assertFilter() {
		return (n, m) -> true;
	}

	// *****************************************

	private void mdAssertNull(String fileName, String message, Object obj) {
		if(assertFilter().test(fileName, message)) {
			try {
				assertNull(obj, message);
			} catch(AssertionError e) {
				assertion(e, fileName, message);
			}
		}
	}

	private void mdAssertNotNull(String fileName, String message, Object obj) {
		if(assertFilter().test(fileName, message)) {
			try {
				assertNotNull(obj, message);
			} catch(AssertionError e) {
				assertion(e, fileName, message);
			}
		}
	}

	// private void mdAssertEquals(String fileName, String message, long expected, long actual) {
	// if(assertFilter().test(fileName, message)) {
	// try {
	// assertEquals(message, expected, actual);
	// } catch(AssertionError e) {
	// assertion(e, fileName, message);
	// }
	// }
	// }

	private void mdAssertEquals(String fileName, String message, double expected, double actual, double delta) {
		if(assertFilter().test(fileName, message)) {
			try {
				assertEquals(expected, actual, delta, message);
			} catch(AssertionError e) {
				assertion(e, fileName, message);
			}
		}
	}

	private void mdAssertEquals(String fileName, String message, Object expected, Object actual) {
		if((expected instanceof Float || expected instanceof Double) && expected != null && actual != null) {
			mdAssertEquals(fileName, message, ((Number)expected).doubleValue(), ((Number)actual).doubleValue(), 0.01D);
			return;
		}
		if(assertFilter().test(fileName, message)) {
			try {
				assertEquals(expected, actual, message);
			} catch(AssertionError e) {
				assertion(e, fileName, message);
			}
		}
	}

	private void assertion(AssertionError error, String fileName, String message) {
		if(DEBUG) {
			System.out.println("FAILED for " + fileName + " : " + message + "  =>  " + error.getMessage());
			FAILED_KEYS.computeIfAbsent(fileName, k -> new HashSet<>())
					.add(message);
		} else {
			throw error;
		}
	}

}
