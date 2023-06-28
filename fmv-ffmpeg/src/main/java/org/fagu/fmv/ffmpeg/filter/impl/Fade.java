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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterComplexCombined;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class Fade extends FilterComplexCombined {

	private Fade(FadeType fadeType, List<FilterComplex> filterComplexs) {
		super("fade-" + fadeType.name().toLowerCase(), filterComplexs);
	}

	public static Fade create(FadeType fadeType, Time startTime, Duration duration) {
		List<FilterComplex> filterComplexs = new ArrayList<>(2);
		filterComplexs.add(FilterComplex.create(FadeVideo.with(fadeType).startTime(startTime).duration(duration)));
		filterComplexs.add(FilterComplex.create(FadeAudio.with(fadeType).startTime(startTime).duration(duration)));
		return new Fade(fadeType, filterComplexs);
	}

	public static Fade in(Time startTime, Duration duration) {
		return create(FadeType.IN, startTime, duration);
	}

	public static Fade out(Time startTime, Duration duration) {
		return create(FadeType.OUT, startTime, duration);
	}

	@Override
	public Set<Type> getTypes() {
		return EnumSet.of(Type.AUDIO, Type.VIDEO);
	}

}
