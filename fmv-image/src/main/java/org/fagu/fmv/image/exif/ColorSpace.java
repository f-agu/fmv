package org.fagu.fmv.image.exif;

/*-
 * #%L
 * fmv-image
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * @author f.agu
 * @created 7 nov. 2019 16:01:42
 */
public class ColorSpace {

	private static final Map<Integer, String> VALUES = new HashMap<>();

	static {
		VALUES.put(0x1, "sRGB");
		VALUES.put(0x2, "Adobe RGB");
		VALUES.put(0xfffd, "Wide Gamut RGB");
		VALUES.put(0xfffe, "ICC Profile");
		VALUES.put(0xffff, "Uncalibrated");
	}

	private ColorSpace() {}

	public static Optional<String> getText(int value) {
		return Optional.ofNullable(VALUES.get(value));
	}

}
