package org.fagu.fmv.image;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.fagu.fmv.media.BaseMetadatasTest;
import org.fagu.fmv.media.ExtractMetadatasOriginalGenerator;
import org.fagu.fmv.media.TestMetadataExtractor;


/**
 * @author f.agu
 * @created 26 nov. 2019 14:28:55
 */
public class ImageMetadatasOriginalGenerator extends ExtractMetadatasOriginalGenerator<ImageMetadatas> {

	public ImageMetadatasOriginalGenerator(File outputFolder, List<TestMetadataExtractor<ImageMetadatas>> testMetadataExtractors) {
		super(outputFolder,
				testMetadataExtractors,
				ImageResourceUtils::extractFile,
				ImageBaseMetadatasTest.class::getResourceAsStream);
	}

	@Override
	protected void generate(BaseMetadatasTest<ImageMetadatas> baseMetadatasTest, String extractorName) throws IOException {
		write(baseMetadatasTest, extractorName, ImageResourceUtils._104);
		write(baseMetadatasTest, extractorName, ImageResourceUtils._203);
		write(baseMetadatasTest, extractorName, ImageResourceUtils.BAD_ASS_TOTTOO_FAIL);
		write(baseMetadatasTest, extractorName, ImageResourceUtils.MULTIPAGE_TIFF);
		write(baseMetadatasTest, extractorName, ImageResourceUtils.PLAN4_550MPIXELS);
		write(baseMetadatasTest, extractorName, ImageResourceUtils.RABBITMQ);
		write(baseMetadatasTest, extractorName, ImageResourceUtils.WEI_ASS);
	}

}
