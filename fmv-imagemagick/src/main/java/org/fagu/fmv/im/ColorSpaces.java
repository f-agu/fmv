package org.fagu.fmv.im;

/*-
 * #%L
 * fmv-imagemagick
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

import java.awt.color.ColorSpace;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * @author Oodrive
 * @author f.agu
 * @created 15 juil. 2019 16:30:46
 */
public class ColorSpaces {

	private static final Map<String, ColorSpace> CS_MAP = new HashMap<>();
	static {
		for(Field field : ColorSpace.class.getFields()) {
			try {
				int modifiers = field.getModifiers();
				if(Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
					String name = field.getName().toLowerCase();
					if(name.startsWith("cs_")) {
						name = name.substring(3);
						CS_MAP.put(name, ColorSpace.getInstance((Integer)field.get(ColorSpace.class)));
					}
				}
			} catch(IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
		CS_MAP.put("cmyk", new ColorSpaceCMYK());
	}

	private ColorSpaces() {}

	public static Optional<ColorSpace> parse(String name) {
		return Optional.ofNullable(CS_MAP.get(name.toLowerCase()));
	}

}
