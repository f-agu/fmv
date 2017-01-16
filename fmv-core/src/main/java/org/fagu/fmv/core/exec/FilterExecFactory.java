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

import org.fagu.fmv.core.exec.filter.GenericFilterExec;


/**
 * @author f.agu
 */
public class FilterExecFactory extends IdentifiableFactory<FilterExec> {

	private static final FilterExecFactory FILTER_EXEC_FACTORY = new FilterExecFactory();

	/**
	 *
	 */
	public FilterExecFactory() {
		super(FilterExec.class);
		try {
			register(FilterExec.class.getPackage().getName());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return
	 */
	public static FilterExecFactory getSingleton() {
		return FILTER_EXEC_FACTORY;
	}

	// *************************************************

	/**
	 * @see org.fagu.fmv.core.exec.Factory#notFound(java.lang.String)
	 */
	@Override
	protected FilterExec notFound(String code) {
		FilterFactory.getSingleton().get(code); // exists ?
		return new GenericFilterExec(code);
	}

}
