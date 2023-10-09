package org.fagu.fmv.media;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 9 oct. 2023 17:51:46
 */
class MetadatasContainerTestCase {

	@Test
	void test() {
		MetadatasContainer metadatasContainer = metadatasContainer();
		assertEquals(2, metadatasContainer.getFirstInteger("exif:SensingMethod").get().intValue());
		assertEquals(4032, metadatasContainer.getFirstInteger("exif:PixelXDimension").get().intValue());
		assertEquals(3024, metadatasContainer.getFirstInteger("exif:PixelYDimension").get().intValue());
	}

	// ***************************************************

	private MetadatasContainer metadatasContainer() {
		return new MetadatasContainer() {

			@Override
			public Map<String, Object> getData() {
				Map<String, Object> map = new HashMap<>();
				map.put("exif:SensingMethod", "2");
				map.put("exif:PixelXDimension", "4032, ");
				map.put("exif:PixelYDimension", "3024,");
				return map;
			}
		};
	}

}
