package org.fagu.fmv.core.exec;

/*
 * #%L
 * fmv-core
 * %%
 * Copyright (C) 2014 - 2015 fagu
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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fagu.fmv.ffmpeg.filter.impl.AudioMix.MixAudioDuration;
import org.fagu.fmv.ffmpeg.utils.Color;
import org.fagu.fmv.ffmpeg.utils.FrameRate;
import org.fagu.fmv.utils.media.Size;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class ObjectInvoker {

	// --------------------------------------------------------

	/**
	 * @author f.agu
	 */
	@FunctionalInterface
	public interface Translator {

		/**
		 * @param str
		 * @return
		 */
		Object translate(String str);
	}

	// --------------------------------------------------------

	/**
	 * 
	 */
	private static final Map<Class<?>, Translator> TRANSLATOR_MAP = new HashMap<>(32);

	static {
		add(String.class, s -> s);
		add(String[].class, s -> new String[] {s});

		// primitives
		add(boolean.class, Boolean::parseBoolean);
		add(Boolean.class, Boolean::parseBoolean);
		add(byte.class, Byte::parseByte);
		add(Byte.class, Byte::parseByte);
		add(short.class, Short::parseShort);
		add(Short.class, Short::parseShort);
		add(int.class, Integer::parseInt);
		add(Integer.class, Integer::parseInt);
		add(long.class, Long::parseLong);
		add(Long.class, Long::parseLong);
		add(float.class, Float::parseFloat);
		add(Float.class, Float::parseFloat);
		add(double.class, Double::parseDouble);
		add(Double.class, Double::parseDouble);

		// specific
		add(Size.class, Size::parse);
		add(Color.class, Color::valueOf);
		add(Duration.class, Duration::parse);
		add(Time.class, Time::parse);
		add(FrameRate.class, FrameRate::parse);
		add(MixAudioDuration.class, s -> MixAudioDuration.valueOf(s.toUpperCase()));
	}

	/**
	 * 
	 */
	private ObjectInvoker() {}

	/**
	 * @param filter
	 * @param name
	 * @return
	 */
	public static List<Method> findMethodsWithOnlyOneParameter(Object object, String name) {
		return Arrays.stream(object.getClass().getMethods()) //
				.filter(m -> name.equalsIgnoreCase(m.getName())) //
				.filter(m -> m.getParameterTypes().length == 1) //
				.collect(Collectors.toList());
	}

	/**
	 * @param object
	 * @param attributeMap
	 */
	public static void invoke(Object object, Map<String, String> attributeMap) {
		attributeMap.forEach((name, value) -> {
			List<Method> methods = ObjectInvoker.findMethodsWithOnlyOneParameter(object, name);
			if(methods.isEmpty()) {
				throw new RuntimeException("Method not found in " + object.getClass().getName() + " for name: " + name + " ; " + attributeMap);
			}
			Method method = methods.stream()
					.filter(m -> m.getParameterTypes()[0] == String.class)
					.findFirst()
					.orElse(methods.get(0));
			if(method != null) {
				try {
					Object objValue = ObjectInvoker.toObject(value, method.getParameterTypes()[0]);
					method.invoke(object, objValue);
				} catch(Exception e) {
					throw new RuntimeException(method + " / " + name + '=' + value, e);
				}
			} else {
				throw new RuntimeException("Method not found " + object.getClass().getName() + " for name: " + name + " ; " + attributeMap);
			}
		});
	}

	/**
	 * @param cls
	 * @param translator
	 */
	public static void add(Class<?> cls, Translator translator) {
		TRANSLATOR_MAP.put(cls, translator);
	}

	/**
	 * @param str
	 * @param cls
	 * @return
	 */
	public static Object toObject(String str, Class<?> cls) {
		Translator translator = TRANSLATOR_MAP.get(cls);
		if(translator == null) {
			throw new RuntimeException("Unknonw type: " + cls + " ; value = " + str);
		}
		return translator.translate(str);
	}

}
