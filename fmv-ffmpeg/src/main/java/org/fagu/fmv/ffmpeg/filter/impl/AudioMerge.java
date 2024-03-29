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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class AudioMerge extends FilterComplex {

	/**
	 * <input, audioStart>
	 */
	private final Map<FilterInput, Time> filterInputMap;

	protected AudioMerge() {
		super("amerge");
		filterInputMap = new LinkedHashMap<>();
	}

	public static AudioMerge build() {
		return new AudioMerge();
	}

	@Override
	public FilterComplex addInput(FilterInput filterInput, Type... types) {
		return addInput(filterInput, null, types);
	}

	public FilterComplex addInput(FilterInput filterInput, Time audioStart, Type... types) {
		filterInputMap.put(filterInput, audioStart);
		return super.addInput(filterInput, types);
	}

	/**
	 * Specify the number of inputs (from 1 to 64) (default 2)
	 * 
	 * @param countInputs
	 * @return
	 */
	public AudioMerge inputs(int countInputs) {
		if(1 > countInputs || countInputs > 64) {
			throw new IllegalArgumentException("inputs must be between 1 and 64: " + countInputs);
		}
		parameter("inputs", Integer.toString(countInputs));
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}

	@Override
	public Optional<Duration> getDuration() {
		return filterInputMap.keySet()
				.stream()
				.map(i -> i.getDuration().orElse(null))
				.filter(Objects::nonNull)
				.min((d1, d2) -> d1.compareTo(d2));
	}

	@Override
	public void upgradeInputProcessor(InputProcessor inputProcessor) {
		Time audioStart = filterInputMap.get(inputProcessor);
		if(audioStart != null) {
			inputProcessor.timeSeek(audioStart);
		}
	}

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
