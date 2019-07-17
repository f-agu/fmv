package org.fagu.fmv.im;

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
		CS_MAP.put("cymk", new ColorSpaceCMYK());
	}

	private ColorSpaces() {}

	public static Optional<ColorSpace> parse(String name) {
		return Optional.ofNullable(CS_MAP.get(name.toLowerCase()));
	}

}
