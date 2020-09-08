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

import java.util.Arrays;
import java.util.Optional;


/**
 * @author f.agu
 * @created 13 nov. 2019 14:32:11
 */
public enum Resolution {

	NONE(1),
	INCHES(2),
	CM(3);

	private final int value;

	private Resolution(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public String getText() {
		return name().toLowerCase();
	}

	public static Optional<Resolution> valueOfText(String text) {
		return Arrays.stream(values())
				.filter(r -> r.getText().equalsIgnoreCase(text))
				.findFirst();
	}

	public static Optional<Resolution> valueOf(int value) {
		return Arrays.stream(values())
				.filter(r -> r.value == value)
				.findFirst();
	}

}
