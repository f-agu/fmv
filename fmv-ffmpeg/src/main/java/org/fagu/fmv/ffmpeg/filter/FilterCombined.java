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

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.operation.Operation;


/**
 * @author f.agu
 */
public abstract class FilterCombined extends AbstractFilter implements Combined {

	private final List<Filter> filters;

	protected FilterCombined(String name, List<Filter> filters) {
		super(name);
		this.filters = Collections.unmodifiableList(new ArrayList<>(filters));
	}

	@Override
	public Filter parameter(String name, String value) {
		throw new RuntimeException("Forbidden, I'm combined");
	}

	public List<Filter> getFilters() {
		return filters;
	}

	@Override
	public String toString() {
		return StringUtils.join(filters, ',');
	}

	// **************************************************

	@Override
	protected void beforeAddAround(Operation<?, ?> operation, FilterNaming filterNaming) {
		for(Filter filter : filters) {
			filter.beforeAdd(operation);
		}
	}

}
