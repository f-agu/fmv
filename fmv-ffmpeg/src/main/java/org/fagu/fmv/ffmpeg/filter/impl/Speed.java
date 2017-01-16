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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterComplexCombined;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class Speed extends FilterComplexCombined {

	/**
	 * @param filterComplexs
	 */
	private Speed(List<FilterComplex> filterComplexs) {
		super(null, filterComplexs);
	}

	/**
	 * @param multiplyBy
	 * @param acceptTypes
	 */
	public static Speed multiply(float multiplyBy, Type... acceptTypes) {
		return multiply(multiplyBy, Arrays.asList(acceptTypes));
	}

	/**
	 * @param multiplyBy
	 * @param acceptTypes
	 */
	public static Speed multiply(float multiplyBy, Collection<Type> acceptTypes) {
		if(multiplyBy <= 0) {
			throw new IllegalArgumentException("multiplyBy must be positive: " + multiplyBy);
		}
		Collection<Type> inAcceptTypes = new HashSet<>(acceptTypes);
		List<FilterComplex> filterComplexs = new ArrayList<>();
		// [0:v] setpts=0.5*PTS [v] ; [0:a] atempo=2.0 [a]
		if(inAcceptTypes.isEmpty() || inAcceptTypes.contains(Type.VIDEO)) {
			SetPTSVideo setPTS = new SetPTSVideo();
			setPTS.speed(multiplyBy);
			filterComplexs.add(FilterComplex.create(setPTS));
		}
		if(inAcceptTypes.isEmpty() || inAcceptTypes.contains(Type.AUDIO)) {
			filterComplexs.add(FilterComplex.create(ATempo.create(multiplyBy)));
		}
		return new Speed(filterComplexs);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Type.valuesSet(this);
	}

}
