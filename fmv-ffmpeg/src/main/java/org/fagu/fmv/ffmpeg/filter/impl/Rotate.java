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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.FilterCombined;
import org.fagu.fmv.ffmpeg.filter.impl.Transpose.Direction;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.media.Rotation;


/**
 * @author f.agu
 */
public class Rotate extends FilterCombined {

	/**
	 * @param filters
	 */
	private Rotate(List<Filter> filters) {
		super("rotate", filters);
		if( ! filters.isEmpty()) {
			((Transpose)filters.get(0)).writeMetadatas(true);
		}
	}

	/**
	 * @param rotation
	 * @return
	 */
	public static Rotate create(Rotation rotation) {
		List<Filter> filters = new ArrayList<>(2);
		if(rotation != null) {
			switch(rotation) {
				case R_180:
					filters.add(new Transpose(Direction.CLOCK));
				case R_90:
					filters.add(new Transpose(Direction.CLOCK));
					break;
				case R_270:
					filters.add(new Transpose(Direction.CCLOCK));
					break;
				case R_0:
				default:
			}
		}
		return new Rotate(filters);
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

}
