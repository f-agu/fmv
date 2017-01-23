package org.fagu.fmv.media;

import java.io.IOException;
import java.util.function.Consumer;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.Soft.SoftExecutor;


/**
 * @author Oodrive
 * @author f.agu
 * @created 23 janv. 2017 09:41:34
 */
public interface MetadatasBuilder<M extends Metadatas, B extends MetadatasBuilder<M, ?>> {

	/**
	 * @param identifySoft
	 * @return
	 */
	B soft(Soft identifySoft);

	/**
	 * @param customizeExecutor
	 * @return
	 */
	B customizeExecutor(Consumer<SoftExecutor> customizeExecutor);

	/**
	 * @return
	 * @throws IOException
	 */
	M extract() throws IOException;
}
