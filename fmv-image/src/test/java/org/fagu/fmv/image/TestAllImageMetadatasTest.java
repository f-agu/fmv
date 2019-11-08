package org.fagu.fmv.image;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


/**
 * @author f.agu
 */
public abstract class TestAllImageMetadatasTest extends AbstractImageMetadatasTest {

	@Test
	public void testFile_bad_ass_tattoo() throws IOException {
		singleDoAndDelete(ImageResourceUtils.BAD_ASS_TOTTOO_FAIL, this::assertMetadatas_BadAssTottooFail);
	}

	@Test
	public void testFile_wei_ass() throws IOException {
		singleDoAndDelete(ImageResourceUtils.WEI_ASS, this::assertMetadatas_WeiAss);
	}

	@Test
	public void testFile_rabbitmq() throws IOException {
		singleDoAndDelete(ImageResourceUtils.RABBITMQ, this::assertMetadatas_Rabbitmq);
	}

	@Test
	public void testFile_plan4() throws IOException {
		singleDoAndDelete(ImageResourceUtils.PLAN4_550MPIXELS, this::assertMetadatas_Plan4_550Mpixels);
	}

	@Test
	public void testFile_Multipage_tiff() throws IOException {
		singleDoAndDelete(ImageResourceUtils.MULTIPAGE_TIFF, this::assertMetadatas_Multipage_tiff);
	}

	@Test
	public void testFile_104() throws IOException {
		singleDoAndDelete(ImageResourceUtils._104, this::assertMetadatas_104);
	}

	@Test
	public void testFile_203() throws IOException {
		singleDoAndDelete(ImageResourceUtils._203, this::assertMetadatas_203);
	}

	@Test
	public void testFailed() throws Exception {
		File file = ImageResourceUtils.extractFile(ImageResourceUtils.NO_IMAGE, "jpg");
		try {
			with(file);
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

}
