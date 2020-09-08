package org.fagu.fmv.media;

/*-
 * #%L
 * fmv-media
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
