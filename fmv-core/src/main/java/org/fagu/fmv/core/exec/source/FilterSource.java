package org.fagu.fmv.core.exec.source;

import java.util.Objects;

import org.fagu.fmv.core.exec.ObjectInvoker;
import org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.GeneratedSource;


/**
 * @author f.agu
 */
public class FilterSource extends AbstractSource {

	private final GeneratedSource generatedSource;

	/**
	 * @param generatedSource
	 */
	public FilterSource(GeneratedSource generatedSource) {
		this.generatedSource = Objects.requireNonNull(generatedSource);
	}

	/**
	 * @see org.fagu.fmv.core.exec.BaseIdentifiable#getCode()
	 */
	@Override
	public String getCode() {
		return generatedSource.name();
	}

	/**
	 * @see org.fagu.fmv.core.exec.Source#createAndAdd(org.fagu.fmv.ffmpeg.executor.FFMPEGExecutorBuilder)
	 */
	@Override
	public FilterInput createAndAdd(FFMPEGExecutorBuilder builder) {
		ObjectInvoker.invoke(generatedSource, attributeMap);
		return builder.addMediaInput(generatedSource.forInput())
				.format("lavfi");
	}

}
