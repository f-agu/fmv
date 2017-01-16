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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public abstract class FilterComplexCombined extends FilterComplexBase implements Combined {

	private final List<FilterComplex> filterComplexs;

	/**
	 * @param name
	 * @param filterComplexs
	 */
	public FilterComplexCombined(String name, List<FilterComplex> filterComplexs) {
		super(name);
		this.filterComplexs = Collections.unmodifiableList(new ArrayList<>(filterComplexs));
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterComplexBase#addInput(org.fagu.fmv.ffmpeg.filter.FilterInput,
	 *      org.fagu.fmv.ffmpeg.operation.Type[])
	 */
	@Override
	public FilterComplexBase addInput(FilterInput filterInput, Type... types) {
		for(FilterComplex filterComplex : filterComplexs) {
			for(Type type : filterComplex.getTypes()) {
				if(types == null || types.length == 0 || ArrayUtils.indexOf(types, type) >= 0) {
					if(filterComplex.contains(type)) {
						if(filterInput instanceof FilterComplexCombined) {
							((FilterComplexCombined)filterInput).filterComplexs.stream() //
							.filter(fc -> fc.contains(type)) //
							.forEach(fc -> filterComplex.addInput(filterInput));
						} else if(filterInput.contains(type)) {
							filterComplex.addInput(filterInput);
						}
					}
				}
			}
		}
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.FilterComplexBase#getOutputKeys()
	 */
	@Override
	public List<OutputKey> getOutputKeys() {
		List<OutputKey> okeys = new ArrayList<>();
		for(FilterComplex filterComplex : filterComplexs) {
			okeys.addAll(filterComplex.getOutputKeys());
		}
		return okeys;
	}

	/**
	 * @return the filters
	 */
	public List<FilterComplex> getFilters() {
		return filterComplexs;
	}

	// **************************************************

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.AbstractFilter#beforeAddAround(org.fagu.fmv.ffmpeg.operation.Operation,
	 *      org.fagu.fmv.ffmpeg.filter.FilterNaming)
	 */
	@Override
	protected void beforeAddAround(Operation<?, ?> operation, FilterNaming filterNaming) {
		for(FilterComplex filterComplex : filterComplexs) {
			filterComplex.beforeAdd(operation);
		}
	}

}
