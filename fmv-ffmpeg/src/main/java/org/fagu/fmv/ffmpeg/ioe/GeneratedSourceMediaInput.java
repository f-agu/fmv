package org.fagu.fmv.ffmpeg.ioe;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 fagu
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

import java.util.Objects;
import java.util.Optional;

import org.fagu.fmv.ffmpeg.filter.GeneratedSource;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Processor;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public class GeneratedSourceMediaInput extends AbstractIOEntity<GeneratedSourceMediaInput> implements MediaInput {

	private final GeneratedSource generatedSource;

	/**
	 * @param generatedSource
	 */
	public GeneratedSourceMediaInput(GeneratedSource generatedSource) {
		super("lavfi");
		this.generatedSource = Objects.requireNonNull(generatedSource);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.ioe.AbstractIOEntity#eventAdded(org.fagu.fmv.ffmpeg.operation.Processor, IOEntity)
	 */
	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		processor.format("lavfi");
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.MediaInput#getDuration()
	 */
	@Override
	public Optional<Duration> getDuration() {
		return generatedSource.getDuration();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return generatedSource.toString();
	}
}
