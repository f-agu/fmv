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
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * @author f.agu
 * @created 26 nov. 2019 14:28:55
 */
public abstract class ExtractMetadatasOriginalGenerator<M extends Metadatas> {

	private final File outputFolder;

	private final List<TestMetadataExtractor<M>> testMetadataExtractors;

	private final FileResourceSupplier fileResourceSupplier;

	private final InputStreamResourceSupplier inputStreamResourceSupplier;

	public ExtractMetadatasOriginalGenerator(File outputFolder,
			List<TestMetadataExtractor<M>> testMetadataExtractors,
			FileResourceSupplier fileResourceSupplier,
			InputStreamResourceSupplier inputStreamResourceSupplier) {
		this.outputFolder = Objects.requireNonNull(outputFolder);
		this.testMetadataExtractors = Objects.requireNonNull(testMetadataExtractors);
		this.fileResourceSupplier = Objects.requireNonNull(fileResourceSupplier);
		this.inputStreamResourceSupplier = Objects.requireNonNull(inputStreamResourceSupplier);
	}

	public void generate() throws IOException {
		for(TestMetadataExtractor<M> testMetadataExtractor : testMetadataExtractors) {
			String extractorName = testMetadataExtractor.getName();
			System.out.println(extractorName);
			BaseMetadatasTest<M> baseMetadatasTest = new BaseMetadatasTest<>(testMetadataExtractor, fileResourceSupplier,
					inputStreamResourceSupplier);
			generate(baseMetadatasTest, extractorName);
		}
	}

	protected abstract void generate(BaseMetadatasTest<M> baseMetadatasTest, String extractor) throws IOException;

	protected void write(BaseMetadatasTest<M> baseMetadatasTest, String extractor, String resourceName) throws IOException {
		System.out.println("    " + resourceName);
		baseMetadatasTest.singleDoAndDelete(
				resourceName,
				writer(resourceName, "file", extractor),
				writer(resourceName, "inputstream", extractor));
	}

	protected Consumer<M> writer(String srcName, String srcType, String extractor) {
		return m -> {
			StringJoiner joiner = new StringJoiner("-");
			joiner.add(FilenameUtils.getBaseName(srcName));
			joiner.add(extractor);
			joiner.add(srcType + "." + FilenameUtils.getExtension(srcName) + ".json");
			File file = new File(outputFolder, joiner.toString());

			System.out.println("        " + srcType + " : " + file);

			String json = m.toJSON();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			Object obj = gson.fromJson(json, Object.class);
			json = gson.toJson(obj);

			outputFolder.mkdirs();
			try (PrintStream printStream = new PrintStream(file)) {
				printStream.print(json);
			} catch(IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}

}
