package org.fagu.fmv.ffmpeg.operation;

import java.util.Objects;

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


/**
 * @author f.agu
 */
public class InputParameters extends IOParameters {

	private String commandPrefixFile;

	private InputProcessorFactory inputProcessorFactory;

	public InputParameters(AbstractOperation<?, ?> abstractOperation) {
		this(abstractOperation, "-i");
	}

	public InputParameters(AbstractOperation<?, ?> abstractOperation, String commandPrefixFile) {
		super(abstractOperation);
		this.commandPrefixFile = commandPrefixFile;
		inputProcessorFactory = InputProcessor::new;
	}

	public InputProcessor addInput(MediaInput input) {
		InputProcessor inputProcessor = inputProcessorFactory.create(this, input, operation.countIOEntity(InputProcessor.class), operation.require());
		add(input, inputProcessor);
		input.eventAdded(inputProcessor, input);
		return inputProcessor;
	}

	public void addAll(InputParameters inputParameters) {
		super.addAll(inputParameters);
	}

	public void setInputProcessorFactory(InputProcessorFactory inputProcessorFactory) {
		this.inputProcessorFactory = Objects.requireNonNull(inputProcessorFactory);
	}

	// *****************************************************

	@Override
	protected String getCommandPrefixFile() {
		return commandPrefixFile;
	}
}
