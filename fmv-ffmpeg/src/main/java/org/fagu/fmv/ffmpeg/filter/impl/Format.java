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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.PixelFormat;


/**
 * @author f.agu
 */
public class Format extends AbstractFilter {

	private static final String CODE = "format";

	protected Format() {
		super(CODE);
	}

	protected Format(PixelFormat... formats) {
		super(CODE);
		set(formats);
	}

	public Format(Collection<PixelFormat> formats) {
		super(CODE);
		set(formats);
	}

	public static Format with(PixelFormat... formats) {
		return new Format(formats);
	}

	public static Format with(Collection<PixelFormat> formats) {
		return new Format(formats);
	}

	public Format set(PixelFormat... formats) {
		return set(Arrays.asList(formats));
	}

	public Format set(Collection<PixelFormat> formats) {
		if(formats.isEmpty()) {
			throw new IllegalArgumentException();
		}
		setMainParameter(StringUtils.join(iterator(formats.iterator()), '|'));
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.VIDEO);
	}

	// *************************************************

	private Iterator<String> iterator(final Iterator<PixelFormat> iterator) {
		return new Iterator<String>() {

			@Override
			public String next() {
				return iterator.next().toString();
			}

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
		};
	}

}
