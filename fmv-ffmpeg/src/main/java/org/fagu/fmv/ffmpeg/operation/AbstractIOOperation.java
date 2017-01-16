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

import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.require.Require;
import org.fagu.fmv.ffmpeg.utils.LogLevel;


/**
 * @author f.agu
 */
public abstract class AbstractIOOperation<R, O> extends AbstractOperation<R, O> {

	private final InputParameters inputParameters;

	private final OutputParameters outputParameters;

	/**
	 * 
	 */
	protected AbstractIOOperation() {
		inputParameters = new InputParameters(this);
		outputParameters = new OutputParameters(this);
	}

	/**
	 * @param filterNaming
	 * @param require
	 */
	protected AbstractIOOperation(FilterNaming filterNaming, Require require) {
		super(filterNaming, require);
		inputParameters = new InputParameters(this);
		outputParameters = new OutputParameters(this);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getInputParameters()
	 */
	public InputParameters getInputParameters() {
		return inputParameters;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.operation.Operation#getOutputParameters()
	 */
	public OutputParameters getOutputParameters() {
		return outputParameters;
	}

	/**
	 * @return
	 */
	public O hideBanner() {
		return addParameter("-hide_banner");
	}

	/**
	 * @return
	 */
	public O logLevel(LogLevel logLevel) {
		return addParameter("-loglevel", logLevel.name().toLowerCase());
	}

	/**
	 * @param name
	 * @return
	 */
	public O addParameter(String name) {
		return add(Parameter.createGlobal(name));
	}

	/**
	 * @param name
	 * @param value
	 * @return
	 */
	public O addParameter(String name, String value) {
		return add(Parameter.createGlobal(name, value));
	}

}
