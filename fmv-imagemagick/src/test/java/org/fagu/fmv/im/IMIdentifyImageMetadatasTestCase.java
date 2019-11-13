package org.fagu.fmv.im;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
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
public class IMIdentifyImageMetadatasTestCase extends TestAllImageMetadatasTest {

	@Test
	public void testMultiple() throws IOException {
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

	@Override
	protected ImageMetadatas with(File file) throws IOException {
		return IMIdentifyImageMetadatas.with(file).extract();
	}

	@Override
	protected ImageMetadatas with(InputStream inputStream) throws IOException {
		return IMIdentifyImageMetadatas.with(inputStream).extract();
	}

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
