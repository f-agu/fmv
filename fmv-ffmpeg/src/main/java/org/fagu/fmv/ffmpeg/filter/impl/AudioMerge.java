package org.fagu.fmv.ffmpeg.filter.impl;

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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.Time;


/**
 * @author f.agu
 */
public class AudioMerge extends FilterComplex {

	// ---------------------------------------------

	/**
	 * @author f.agu
	 */
	public enum MixAudioDuration {
		LONGEST, SHORTEST, FIRST
	}

	// ---------------------------------------------

	/**
	 * <input, audioStart>
	 */
	private final Map<FilterInput, Time> filterInputMap;

	/**
	 * 
	 */
	protected AudioMerge() {
		super("amerge");
		filterInputMap = new LinkedHashMap<>();
	}

	/**
	 * @return
	 */
	public static AudioMerge build() {
		return new AudioMerge();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterComplex#addInput(org.fagu.fmv.ffmpeg.filter.FilterInput,
	 *      org.fagu.fmv.ffmpeg.operation.Type[])
	 */
	@Override
	public FilterComplex addInput(FilterInput filterInput, Type... types) {
		return addInput(filterInput, null, types);
	}

	/**
	 * @param filterInput
	 * @param startTime
	 * @return
	 */
	public FilterComplex addInput(FilterInput filterInput, Time audioStart, Type... types) {
		filterInputMap.put(filterInput, audioStart);
		return super.addInput(filterInput, types);
	}

	/**
	 * @param countInputs
	 * @return
	 */
	public AudioMerge inputs(int countInputs) {
		parameter("inputs", Integer.toString(countInputs));
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterComplexBase#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#upgradeInputProcessor(org.fagu.fmv.ffmpeg.operation.InputProcessor)
	 */
	@Override
	public void upgradeInputProcessor(InputProcessor inputProcessor) {
		Time audioStart = filterInputMap.get(inputProcessor);
		if(audioStart != null) {
			inputProcessor.timeSeek(audioStart);
		}
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterComplexBase#toString()
	 */
	@Override
	public String toString() {
		if( ! containsParameter("inputs")) {
			int countVariousInput = countVariousInput();
			if(countVariousInput == 0) {
				throw new RuntimeException("Missing input. Use addInput(..)");
			}
			inputs(countVariousInput);
		}
		return super.toString();
	}

}
