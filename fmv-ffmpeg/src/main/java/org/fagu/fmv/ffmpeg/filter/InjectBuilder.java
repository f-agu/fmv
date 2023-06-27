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

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.fagu.fmv.ffmpeg.filter.FilterComplexBase.In;
import org.fagu.fmv.ffmpeg.operation.Operation;


/**
 * @author f.agu
 */
public class InjectBuilder {

	private InjectBuilder() {}

	public static void inject(Filter toFilter, Operation<?, ?> operation) {
		if(toFilter == null) {
			return;
		}

		// current toFilter
		Class<? extends Filter> cls = toFilter.getClass();
		for(Field field : FieldUtils.getAllFieldsList(cls)) {
			if(field.getType() == Operation.class) {
				field.setAccessible(true);
				try {
					if(field.get(toFilter) == null) {
						toFilter.beforeAdd(operation);
						// field.set(toFilter, builder);
					}
				} catch(IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch(IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}

		// depends filters
		if(toFilter instanceof FilterComplexBase) {
			Map<IOKey, In> inputMap = ((FilterComplexBase)toFilter).getInputMap();
			for(In in : inputMap.values()) {
				FilterInput filterInput = in.getFilterInput();
				if(filterInput instanceof Filter) {
					inject((Filter)filterInput, operation);
				}
			}
		}
	}
}
