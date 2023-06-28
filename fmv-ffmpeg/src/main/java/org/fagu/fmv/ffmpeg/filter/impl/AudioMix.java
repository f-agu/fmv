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
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class AudioMix extends FilterComplex {

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

	protected AudioMix() {
		super("amix");
		filterInputMap = new LinkedHashMap<>();
	}

	public static AudioMix build() {
		return new AudioMix();
	}

	@Override
	public FilterComplex addInput(FilterInput filterInput, Type... types) {
		return addInput(filterInput, null, types);
	}

	public FilterComplex addInput(FilterInput filterInput, Time audioStart, Type... types) {
		filterInputMap.put(filterInput, audioStart);
		return super.addInput(filterInput, types);
	}

	public AudioMix inputs(int countInputs) {
		parameter("inputs", Integer.toString(countInputs));
		return this;
	}

	public AudioMix duration(MixAudioDuration mixAudioDuration) {
		parameter("duration", mixAudioDuration.name().toLowerCase());
		return this;
	}

	public AudioMix dropoutTransition(Duration duration) {
		parameter("dropout_transition", Double.toString(duration.toSeconds()));
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
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
