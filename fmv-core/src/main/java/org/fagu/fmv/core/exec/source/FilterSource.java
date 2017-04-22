package org.fagu.fmv.core.exec.source;

/*-
 * #%L
 * fmv-core
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
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
