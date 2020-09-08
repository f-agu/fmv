package org.fagu.fmv.soft.mplayer;

/*-
 * #%L
 * fmv-soft-auto
 * %%
 * Copyright (C) 2014 - 2020 fagu
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
import java.util.HashMap;
import java.util.Map;


/**
 * @author f.agu
 * @created 5 juin 2017 13:28:15
 */
public abstract class Stream {

	private final int num;

	private final Map<String, String> properties;

	public Stream(int num, Map<String, String> properties) {
		this.num = num;
		this.properties = Collections.unmodifiableMap(new HashMap<>(properties));
	}

	public int getNum() {
		return num;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public String getLanguage() {
		return properties.get("language");
	}
}
