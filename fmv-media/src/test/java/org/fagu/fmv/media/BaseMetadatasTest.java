package org.fagu.fmv.media;

import static org.junit.Assert.fail;

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
