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

import org.fagu.fmv.ffmpeg.filter.AbstractFilter;
import org.fagu.fmv.ffmpeg.operation.Type;


/**
 * @author f.agu
 */
public class ATempo extends AbstractFilter {

	protected ATempo() {
		super("atempo");
	}

	protected ATempo(float factor) {
		this();
		speed(factor);
	}

	public static ATempo build() {
		return new ATempo();
	}

	public ATempo speed(float factor) {
		if(factor < 0.5 || 2 < factor) {
			throw new IllegalArgumentException("must be in the [0.5, 2.0] range. Or use static method 'speed(float)'");
		}
		setMainParameter(Float.toString(factor));
		return this;
	}

	@Override
	public Set<Type> getTypes() {
		return Collections.singleton(Type.AUDIO);
	}

	public static List<ATempo> create(float factor) {
		float n = factor;
		if(0.5 < n && n < 2) {
			return Collections.singletonList(new ATempo(factor));
		}
		List<ATempo> list = new ArrayList<>();
		if(factor > 1) {
			do {
				list.add(new ATempo(2F));
			} while((n /= 2) > 2);
			if(n != 0 && n != 1) {
				list.add(new ATempo(n));
			}
		} else {
			do {
				list.add(new ATempo(0.5F));
			} while((n /= 0.5) < 0.5);
			if(n != 0 && n != 1) {
				list.add(new ATempo(n));
			}
		}
		return list;
	}
}
