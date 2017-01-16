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


import org.fagu.fmv.ffmpeg.filter.AbstractFilter;


/**
 * @author f.agu
 */
public abstract class SetTB<T> extends AbstractFilter {

	/**
	 * @param name
	 */
	protected SetTB(String name) {
		super(name);
	}

	/**
	 * Available constant :<br>
	 * <ul>
	 * <li>intb : the input timebase</li>
	 * <li>sr : the sample rate, audio only</li>
	 * </ul>
	 * 
	 * @param expr
	 * @return
	 */
	public T expr(String expr) {
		setMainParameter(expr);
		return getThis();
	}

	// **********************************************

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T getThis() {
		return (T)this;
	}

}
