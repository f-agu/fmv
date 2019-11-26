package org.fagu.fmv.media;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 * @created 26 nov. 2019 15:14:58
 */
public interface TestMetadataExtractor<M extends Metadatas> {

	default String getName() {
		String name = getClass().getSimpleName();
		return StringUtils.substringBefore(name, "TestMetadataExtractor");
	}

	M extract(File file, String name) throws IOException;

	M extract(InputStream inputStream, String name) throws IOException;

}
