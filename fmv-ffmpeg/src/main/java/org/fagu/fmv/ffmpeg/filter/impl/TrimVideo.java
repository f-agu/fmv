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
import java.util.Set;

import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class TrimVideo extends Trim<TrimVideo> {

	/**
	 * @param name
	 */
	protected TrimVideo() {
		super("trim");
	}

	/**
	 * @return
	 */
	public static TrimVideo build() {
		return new TrimVideo();
	}

	/**
	 * @param number
	 */
	public TrimVideo startFrame(int number) {
		parameter("start_frame", Integer.toString(number));
		return this;
	}

	/**
	 * @param number
	 */
	public TrimVideo endFrame(int number) {
		parameter("end_frame", Integer.toString(number));
		return this;
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()
	 */
	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}
}
