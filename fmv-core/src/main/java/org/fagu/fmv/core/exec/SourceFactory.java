package org.fagu.fmv.core.exec;

/*
 * #%L
 * fmv-core
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import java.io.IOException;

import org.fagu.fmv.core.exec.source.FilterSource;
import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.ffmpeg.filter.GeneratedSource;


/**
 * @author f.agu
 */
public class SourceFactory extends IdentifiableFactory<Source> {

	private static final SourceFactory SOURCE_FACTORY = new SourceFactory();

	/**
	 *
	 */
	public SourceFactory() {
		super(Source.class);
		try {
			register(Source.class.getPackage().getName());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return
	 */
	public static SourceFactory getSingleton() {
		return SOURCE_FACTORY;
	}

	// *************************************************

	/**
	 * @see org.fagu.fmv.core.exec.Factory#notFound(java.lang.String)
	 */
	@Override
	protected Source notFound(String code) {
		Filter filter = FilterFactory.getSingleton().get(code); // exists ?
		if(filter instanceof GeneratedSource) {
			return new FilterSource((GeneratedSource)filter);
		}
		throw new IllegalStateException(code + " is not a generated source");
	}

}
