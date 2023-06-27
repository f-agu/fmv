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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public abstract class FilterComplex extends FilterComplexBase {

	protected FilterComplex(String name) {
		this(name, true);
	}

	protected FilterComplex(String name, boolean autoGenerateOutputLabel) {
		super(name);
		if(autoGenerateOutputLabel) {
			addOutput();
		}
	}

	public static FilterComplex create(Filter... filters) {
		return create(Arrays.asList(filters));
	}

	public static FilterComplex create(final Collection<? extends Filter> filters) {
		final Set<Type> inTypes = new HashSet<>();
		for(Filter filter : filters) {
			inTypes.addAll(filter.getTypes());
			if(inTypes.size() > 1) {
				throw new IllegalArgumentException("Filters must be same type: " + filters);
			}
		}
		FilterComplex filterComplex = new FilterComplex("f") {

			@Override
			public FilterComplex addInput(FilterInput filterInput, Type... types) {
				if(types.length == 0) {
					super.addInput(filterInput, inTypes.toArray(new Type[1]));
				} else {
					super.addInput(filterInput, types);
				}
				return this;
			}

			@Override
			public List<MediaInput> getInputs() {
				List<MediaInput> inputs = super.getInputs();
				for(Filter filter : filters) {
					if(filter instanceof MediaInput) {
						inputs.add((MediaInput)filter);
					}
				}
				return inputs;
			}

			@Override
			public Set<Type> getTypes() {
				return inTypes;
			}

			@Override
			public String toString() {
				return toString(StringUtils.join(filters, ','));
			}

			// *******************************************

			@Override
			protected boolean hasExplicitType() {
				return false;
			}
		};

		return filterComplex;
	}

	@Override
	public FilterComplex addInput(FilterInput filterInput, Type... types) {
		super.addInput(filterInput, types);
		return this;
	}
}
