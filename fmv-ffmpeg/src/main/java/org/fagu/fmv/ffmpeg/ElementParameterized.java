package org.fagu.fmv.ffmpeg;

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

import org.fagu.fmv.ffmpeg.operation.Parameter;
import org.fagu.fmv.ffmpeg.operation.Parameter.Way;


/**
 * @author f.agu
 */
public abstract class ElementParameterized<M> extends Element<M> {

	private Way defaultWay = Way.BEFORE;

	/**
	 * @param name
	 */
	public ElementParameterized(String name) {
		super(name);
	}

	/**
	 * @param name
	 */
	public M parameter(String name) {
		return parameter(name, null);
	}

	/**
	 * @param parameter
	 */
	public M parameter(Parameter parameter) {
		parameterMap.put(parameter.getName(), new PseudoParam(parameter));
		return getMThis();
	}

	/**
	 * @return
	 */
	public Way getDefaultWay() {
		return defaultWay;
	}

	/**
	 * @param defaultWay
	 */
	public void setDefaultWay(Way defaultWay) {
		this.defaultWay = defaultWay;
	}

}
