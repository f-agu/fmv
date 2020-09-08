package org.fagu.fmv.soft.mediainfo;

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

import java.util.OptionalDouble;
import java.util.OptionalInt;


/**
 * @author f.agu
 * @created 7 avr. 2018 15:15:50
 */
public interface ParsableType {

	String getString(String key);

	default OptionalInt getInt(String key) {
		String value = getString(key);
		if(value == null) {
			return OptionalInt.empty();
		}
		try {
			return OptionalInt.of(Integer.parseInt(value));
		} catch(NumberFormatException e) {
			return OptionalInt.empty();
		}
	}

	default OptionalDouble getDouble(String key) {
		String value = getString(key);
		if(value == null) {
			return OptionalDouble.empty();
		}
		try {
			return OptionalDouble.of(Double.parseDouble(value));
		} catch(NumberFormatException e) {
			return OptionalDouble.empty();
		}
	}

}
