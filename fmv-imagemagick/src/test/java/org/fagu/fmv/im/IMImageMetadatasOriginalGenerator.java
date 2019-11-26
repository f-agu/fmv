package org.fagu.fmv.im;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fagu.fmv.image.ImageMetadatas;
import org.fagu.fmv.image.ImageMetadatasOriginalGenerator;
import org.fagu.fmv.media.TestMetadataExtractor;


/**
 * @author f.agu
 * @created 26 nov. 2019 14:56:38
 */
public class IMImageMetadatasOriginalGenerator {

	public static void main(String[] args) throws IOException {
		File outputFolder = new File("c:\\tmp\\metadata");
		List<TestMetadataExtractor<ImageMetadatas>> extractors = new ArrayList<>();
		extractors.add(new IMConvertImageTestMetadataExtractor());
		extractors.add(new IMIdentifyImageTestMetadataExtractor());
		new ImageMetadatasOriginalGenerator(outputFolder, extractors).generate();
	}

}
