package org.fagu.fmv.ffmpeg.utils.srcgen;

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


/**
 * @author f.agu
 */
public class Param extends AbstractOption {

	private ParamType paramType;

	private List<ParamValue> values;

	public Param(String name, ParamType paramType, Flags flags, String description) {
		super(name, flags, description);
		this.paramType = paramType;
	}

	public void addValue(ParamValue paramValue) {
		if(values == null) {
			values = new ArrayList<>();
		}
		values.add(paramValue);
	}

	public ParamType getType() {
		return paramType;
	}

	public List<ParamValue> getValues() {
		return values == null ? Collections.emptyList() : values;
	}

	@Override
	public String toString() {
		return getName() + (values != null ? values.toString() : "");
	}

}
