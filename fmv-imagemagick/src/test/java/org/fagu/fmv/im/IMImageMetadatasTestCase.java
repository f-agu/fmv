package org.fagu.fmv.im;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.fagu.fmv.image.ImageMetadatas;
import org.fagu.fmv.image.ImageResourceUtils;
import org.fagu.fmv.image.TestAllImageMetadatasTest;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author f.agu
 */
public class IMImageMetadatasTestCase extends TestAllImageMetadatasTest {

	@Test
	public void testMultiple() throws IOException {
		File file1 = ImageResourceUtils.extractFile("bad-ass-tattoo-fail.jpg");
		File file2 = ImageResourceUtils.extractFile("wei-ass.jpg");

		try {
			Map<File, IMImageMetadatas> map = IMImageMetadatas.with(Arrays.asList(file2, file1)).extractAll();
			Iterator<IMImageMetadatas> iterator = map.values().iterator();
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
			IMImageMetadatas.with(file).extract();
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
					ImageMetadatas extract = IMImageMetadatas.extractSingleton(file);
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
		return IMImageMetadatas.with(file).extract();
	}

	@Override
	protected ImageMetadatas with(InputStream inputStream) throws IOException {
		return IMImageMetadatas.with(inputStream).extract();
	}

}
