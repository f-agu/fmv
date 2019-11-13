package org.fagu.fmv.image.exif;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * @author f.agu
 * @created 7 nov. 2019 15:54:31
 */
public class Compression {

	private static final Map<Integer, String> VALUES = new HashMap<>();

	static {
		VALUES.put(1, "Uncompressed");
		VALUES.put(2, "CCITT 1D");
		VALUES.put(3, "T4/Group 3 Fax");
		VALUES.put(4, "T6/Group 4 Fax");
		VALUES.put(5, "LZW");
		VALUES.put(6, "JPEG (old-style)");
		VALUES.put(7, "JPEG");
		VALUES.put(8, "Adobe Deflate");
		VALUES.put(9, "JBIG B&W");
		VALUES.put(10, "JBIG Color");
		VALUES.put(99, "JPEG");
		VALUES.put(262, "Kodak 262");
		VALUES.put(32766, "Next");
		VALUES.put(32767, "Sony ARW Compressed");
		VALUES.put(32769, "Packed RAW");
		VALUES.put(32770, "Samsung SRW Compressed");
		VALUES.put(32771, "CCIRLEW");
		VALUES.put(32772, "Samsung SRW Compressed 2");
		VALUES.put(32773, "PackBits");
		VALUES.put(32809, "Thunderscan");
		VALUES.put(32867, "Kodak KDC Compressed");
		VALUES.put(32895, "IT8CTPAD");
		VALUES.put(32896, "IT8LW");
		VALUES.put(32897, "IT8MP");
		VALUES.put(32898, "IT8BL");
		VALUES.put(32908, "PixarFilm");
		VALUES.put(32909, "PixarLog");
		VALUES.put(32946, "Deflate");
		VALUES.put(32947, "DCS");
		VALUES.put(33003, "Aperio JPEG 2000 YCbCr");
		VALUES.put(33005, "Aperio JPEG 2000 RGB");
		VALUES.put(34661, "JBIG");
		VALUES.put(34676, "SGILog");
		VALUES.put(34677, "SGILog24");
		VALUES.put(34712, "JPEG 2000");
		VALUES.put(34713, "Nikon NEF Compressed");
		VALUES.put(34715, "JBIG2 TIFF FX");
		VALUES.put(34718, "Microsoft Document Imaging (MDI) Binary Level Codec");
		VALUES.put(34719, "Microsoft Document Imaging (MDI) Progressive Transform Codec");
		VALUES.put(34720, "Microsoft Document Imaging (MDI) Vector");
		VALUES.put(34887, "ESRI Lerc");
		VALUES.put(34892, "Lossy JPEG");
		VALUES.put(34925, "LZMA2");
		VALUES.put(34926, "Zstd");
		VALUES.put(34927, "WebP");
		VALUES.put(34933, "PNG");
		VALUES.put(34934, "JPEG XR");
		VALUES.put(65000, "Kodak DCR Compressed");
		VALUES.put(65535, "Pentax PEF Compressed");
	}

	private Compression() {}

	public static Optional<String> getText(int value) {
		return Optional.ofNullable(VALUES.get(value));
	}

}
