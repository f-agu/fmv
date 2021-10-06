package org.fagu.fmv.media;

import static org.junit.jupiter.api.Assertions.fail;

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
import java.util.Objects;
import java.util.function.Consumer;


/**
 * @author f.agu
 */
public class BaseMetadatasTest<M extends Metadatas> {

	private final FileResourceSupplier fileResourceSupplier;

	private final InputStreamResourceSupplier inputStreamResourceSupplier;

	protected final TestMetadataExtractor<M> testMetadataExtractor;

	public BaseMetadatasTest(TestMetadataExtractor<M> testMetadataExtractor,
			FileResourceSupplier fileResourceSupplier,
			InputStreamResourceSupplier inputStreamResourceSupplier) {
		this.testMetadataExtractor = Objects.requireNonNull(testMetadataExtractor);
		this.fileResourceSupplier = Objects.requireNonNull(fileResourceSupplier);
		this.inputStreamResourceSupplier = Objects.requireNonNull(inputStreamResourceSupplier);
	}

	public void singleDoAndDelete(String resourceName, Consumer<M> consumer) throws IOException {
		singleDoAndDelete(resourceName, consumer, consumer);
	}

	public void singleDoAndDelete(String resourceName, Consumer<M> fileConsumer, Consumer<M> isConsumer)
			throws IOException {
		// by file
		File file = fileResourceSupplier.supply(resourceName);
		try {
			M metadatas = testMetadataExtractor.extract(file, resourceName);
			if(metadatas != null) {
				fileConsumer.accept(metadatas);
			}
		} finally {
			if(file != null && ! file.delete()) {
				fail("Unable to delete " + file);
			}
		}

		// by inputStream
		try (InputStream inputStream = inputStreamResourceSupplier.supply(resourceName)) {
			M metadatas = testMetadataExtractor.extract(inputStream, resourceName);
			if(metadatas != null) {
				isConsumer.accept(metadatas);
			}
		}
	}

}
