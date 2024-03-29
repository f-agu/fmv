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
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.filter.GeneratedSource;
import org.fagu.fmv.ffmpeg.ioe.GeneratedSourceMediaInput;
import org.fagu.fmv.ffmpeg.operation.IOEntity;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Processor;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.time.Duration;


/**
 * @author f.agu
 */
public class AudioGenerator extends AbstractFilter implements MediaInput, GeneratedSource {

	private Duration duration;

	protected AudioGenerator() {
		super("aevalsrc");
	}

	public static AudioGenerator build() {
		return new AudioGenerator();
	}

	public AudioGenerator silence() {
		expr("0");
		return this;
	}

	public AudioGenerator sinOnechannel(int frequency) {
		expr("sin(" + frequency + "*2*PI*t)");
		return this;
	}

	public AudioGenerator sinCosTwochannel(int channel1Frequency, int channel2Frequency) {
		expr("sin(" + channel1Frequency + "*2*PI*t)", "cos(" + channel2Frequency + "*2*PI*t)");
		return this;
	}

	public AudioGenerator expr(String... exprs) {
		parameter("exprs", "'" + StringUtils.join(exprs, '|') + "'");
		return this;
	}

	public AudioGenerator duration(Duration duration) {
		this.duration = duration;
		parameter("d", Double.toString(duration.toSeconds()));
		return this;
	}

	public AudioGenerator sampleRate(int rate) {
		parameter("s", Integer.toString(rate));
		return this;
	}

	@Override
	public void eventAdded(Processor<?> processor, IOEntity ioEntity) {
		// NOTHING
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}

	@Override
	public MediaInput forInput() {
		return new GeneratedSourceMediaInput(this);
	}

	@Override
	public Optional<Duration> getDuration() {
		return Optional.ofNullable(duration);
	}

}
