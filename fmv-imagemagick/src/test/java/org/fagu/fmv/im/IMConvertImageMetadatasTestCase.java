package org.fagu.fmv.im;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import org.fagu.fmv.image.ImageMetadatas;
import org.fagu.fmv.image.ImageResourceUtils;
import org.fagu.fmv.image.TestAllImageMetadatasTest;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class IMConvertImageMetadatasTestCase extends TestAllImageMetadatasTest {

	public IMConvertImageMetadatasTestCase() {
		super(new IMConvertImageTestMetadataExtractor());
	}

	@Test
	public void testMultiple() throws IOException {
		File file1 = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File file2 = ImageResourceUtils.extractFile("wei-ass.jpg");

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