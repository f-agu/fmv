package org.fagu.fmv.im;

/*-
 * #%L
 * fmv-imagemagick
 * %%
 * Copyright (C) 2014 - 2020 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.fagu.fmv.image.ImageMetadatas;
import org.fagu.fmv.media.TestMetadataExtractor;


/**
 * @author f.agu
 * @created 26 nov. 2019 14:36:03
 */
public class IMConvertImageTestMetadataExtractor implements TestMetadataExtractor<ImageMetadatas> {

	@Override
	public String getName() {
		return "im-convert";
	}

	@Override
	public ImageMetadatas extract(File file, String name) throws IOException {
		return IMConvertImageMetadatas.with(file)
				// .logger(cmd -> System.out.println(cmd))
				.extract();
	}

	@Override
	public ImageMetadatas extract(InputStream inputStream, String name) throws IOException {
		return IMConvertImageMetadatas.with(inputStream)
				// .logger(cmd -> System.out.println(cmd))
				.extract();
	}

}
