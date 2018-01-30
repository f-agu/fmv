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
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.fagu.fmv.ffmpeg.filter.Filter;


/**
 * @author f.agu
 */
public class FilterFactory extends Factory<Filter> {

	private static final FilterFactory FILTER_FACTORY = new FilterFactory();

	/**
	 *
	 */
	public FilterFactory() {
		super(Filter.class, Filter::name);
		try {
			register(Filter.class.getPackage().getName());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return
	 */
	public static FilterFactory getSingleton() {
		return FILTER_FACTORY;
	}

	/**
	 * @param type
	 * @return
	 */
	public Supplier<Filter> getSupplier(String type) {
		Class<Filter> filterClass = getClass(type);
		if(filterClass == null) {
			throw new IllegalArgumentException("Type undefined: " + type);
		}
		return () -> instanciate(filterClass);
	}

	// ********************************************

	/**
	 * @see org.fagu.fmv.core.exec.Factory#instanciate(java.lang.Class)
	 */
	@Override
	protected Filter instanciate(Class<Filter> cls) {
		try {
			Method buildMethod = cls.getDeclaredMethod("build");
			return (Filter)buildMethod.invoke(cls);
		} catch(Exception e) {
			return null;
		}
	}

}
