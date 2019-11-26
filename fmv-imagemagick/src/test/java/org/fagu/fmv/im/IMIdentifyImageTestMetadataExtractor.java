package org.fagu.fmv.im;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.fagu.fmv.image.ImageMetadatas;
import org.fagu.fmv.media.TestMetadataExtractor;


/**
 * @author f.agu
 * @created 26 nov. 2019 14:36:03
 */
public class IMIdentifyImageTestMetadataExtractor implements TestMetadataExtractor<ImageMetadatas> {

	@Override
	public String getName() {
		return "im-identify";
	}

	@Override
	public ImageMetadatas extract(File file, String name) throws IOException {
		return IMIdentifyImageMetadatas.with(file).extract();
	}

	@Override
	public ImageMetadatas extract(InputStream inputStream, String name) throws IOException {
		return IMIdentifyImageMetadatas.with(inputStream).extract();
	}

}
