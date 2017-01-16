package org.fagu.fmv.ffmpeg.operation;

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

import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.require.Require;


/**
 * @author f.agu
 */
public class OutputParameters extends IOParameters {

	/**
	 *
	 */
	private OutputProcessorFactory outputProcessorFactory;

	/**
	 * @param abstractOperation
	 */
	public OutputParameters(AbstractOperation<?, ?> abstractOperation) {
		super(abstractOperation);
		outputProcessorFactory = new OutputProcessorFactory() {

			/**
			 * @see org.fagu.fmv.ffmpeg.operation.OutputProcessorFactory#create(org.fagu.fmv.ffmpeg.operation.OutputParameters,
			 *      org.fagu.fmv.ffmpeg.operation.MediaOutput, org.fagu.fmv.ffmpeg.filter.FilterNaming, int,
			 *      org.fagu.fmv.ffmpeg.require.Require)
			 */
			@Override
			public OutputProcessor create(OutputParameters outputParameters, MediaOutput output, FilterNaming filterNaming, int index, Require require) {
				return new OutputProcessor(outputParameters, output, filterNaming, index, require);
			}
		};
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.IOParameters#toArguments(java.util.Collection)
	 */
	@Override
	public void toArguments(Collection<String> commands) {
		if(getIOEntities().isEmpty()) {
			return;
		}
		super.toArguments(commands);
	}

	/**
	 * @param output
	 * @return
	 */
	public OutputProcessor addOutput(MediaOutput output) {
		OutputProcessor outputProcessor = outputProcessorFactory.create(this, output, operation.getFilterNaming(), operation.countIOEntity(OutputProcessor.class), operation.require());
		add(output, outputProcessor);
		output.eventAdded(outputProcessor, output);
		return outputProcessor;
	}

	/**
	 * @param outputParameters
	 */
	public void addAll(OutputParameters outputParameters) {
		super.addAll(outputParameters);
	}

	/**
	 * @param outputProcessorFactory
	 */
	public void setOutputProcessorFactory(OutputProcessorFactory outputProcessorFactory) {
		this.outputProcessorFactory = Objects.requireNonNull(outputProcessorFactory);
	}

	// ****************************************************

	/**
	 * @return
	 */
	@Override
	protected String getCommandPrefixFile() {
		return StringUtils.EMPTY;
	}
}
