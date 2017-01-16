package org.fagu.fmv.ffmpeg.filter;

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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class OutputKey implements FilterInput {

	private final FilterInput filterInput;

	private final Label label;

	/**
	 * @param filterInput
	 * @param label
	 */
	public OutputKey(FilterInput filterInput, Label label) {
		this.filterInput = Objects.requireNonNull(filterInput);
		this.label = Objects.requireNonNull(label);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#getOutputKeys()
	 */
	@Override
	public List<OutputKey> getOutputKeys() {
		return Collections.singletonList(this);
	}

	/**
	 * @return
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#getInputs()
	 */
	@Override
	public List<MediaInput> getInputs() {
		return filterInput.getInputs();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#getInputKeys()
	 */
	@Override
	public Set<IOKey> getInputKeys() {
		return filterInput.getInputKeys();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterInput#contains(org.fagu.fmv.ffmpeg.operation.Type)
	 */
	@Override
	public boolean contains(Type type) {
		return filterInput.contains(type);
	}

}
