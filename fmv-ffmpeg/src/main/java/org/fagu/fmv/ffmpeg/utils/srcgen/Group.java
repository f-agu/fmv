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
import java.util.Iterator;
import java.util.List;


/**
 * @author f.agu
 */
public class Group extends AbstractOption {

	private List<Param> params;

	public Group(String name, String description) {
		super(name, null, description);
	}

	@Override
	public Flags getFlags() {
		if(params == null) {
			return Flags.nothing();
		}
		Iterator<Param> paramsIterator = params.iterator();
		Flags flags = paramsIterator.next().getFlags();
		while(paramsIterator.hasNext()) {
			flags = flags.add(paramsIterator.next().getFlags());
		}
		return flags;
	}

	public void addParam(Param param) {
		if(params == null) {
			params = new ArrayList<>();
		}
		params.add(param);
	}

	public List<Param> getParams() {
		return params == null ? Collections.emptyList() : params;
	}

	@Override
	public String toString() {
		return getName() + (params != null ? params.toString() : "");
	}

}
