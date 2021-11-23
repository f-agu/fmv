package org.fagu.fmv.ffmpeg.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fagu.fmv.ffmpeg.filter.Filters.IOType;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.ffmpeg.utils.srcgen.ClassNameUtils;
import org.fagu.fmv.utils.ClassResolver;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * @author f.agu
 */
@Disabled
class FiltersGenerator {

	@Test
	void generator() {
		Map<String, org.fagu.fmv.ffmpeg.filter.Filter> findFilter = findFilter();
		for(Filters filter : Filters.available()) {
			// if(findFilter.containsKey(filter.getName())) {
			String name = filter.getName();
			String fieldName = ClassNameUtils.fieldStatic(name);

			System.out.println("/**");
			System.out.println(" * " + filter.getInputType() + "->" + filter.getOutputType() + ", " + filter.getDescription());
			System.out.println(" */");
			System.out.println("public static final Filters " + fieldName + " = new Filters(\"" + name + "\");");

			org.fagu.fmv.ffmpeg.filter.Filter f = findFilter.get(filter.getName());
			if(f != null && ! isApplicable(f, filter)) {
				throw new RuntimeException(f.getClass().getName());
			}
			// }
		}
	}

	@Test
	void testCache() {
		assertTrue(Filters.exists("scale"));
		assertEquals("", "");
		// assertEquals(12, PixelFormat.YUV420P.getBitsPerPixel());
		// assertEquals(3, PixelFormat.YUV420P.getNbComponents());
		// assertTrue(PixelFormat.YUV420P.isSupportedInput());
		// assertTrue(PixelFormat.YUV420P.isSupportedOutput());
		// assertFalse(PixelFormat.YUV420P.isBitstream());
		// assertFalse(PixelFormat.YUV420P.isHardwareAccelerated());
		// assertFalse(PixelFormat.YUV420P.isPaletted());
		// assertTrue(PixelFormat.MONOW.isSupportedInput());
		// assertFalse(PixelFormat.PAL8.isSupportedOutput());
	}

	// ***********************************************

	private boolean isApplicable(org.fagu.fmv.ffmpeg.filter.Filter filterI, Filters filterD) {
		boolean check = true;
		if(filterI instanceof GeneratedSource) {
			if( ! filterD.getInputType().isSourceOrSink()) {
				System.err.println("Source"); // TODO
			}
		} else {
			check &= isApplicable(filterI.getInputTypes(), filterD.getInputType());
		}
		check &= isApplicable(filterI.getOutputTypes(), filterD.getOutputType());

		return check;
	}

	private boolean isApplicable(Collection<Type> types, IOType ioType) {
		if(ioType.isNumber()) {
			return true;
		}
		for(Type type : types) {
			switch(type) {
				case AUDIO:
					if( ! ioType.isAudio()) {
						System.err.println("Audio != " + ioType);
						return false;
					}
					break;
				case VIDEO:
					if( ! ioType.isVideo()) {
						System.err.println("Video != " + ioType);
						return false;
					}
					break;
				default:
			}
		}
		return true;
	}

	private Map<String, org.fagu.fmv.ffmpeg.filter.Filter> findFilter() {
		Map<String, org.fagu.fmv.ffmpeg.filter.Filter> filters = new HashMap<>();
		ClassResolver classResolver = new ClassResolver();
		try {
			for(Class<?> findCls : classResolver.find("org.fagu.fmv.ffmpeg", c -> {
				if(org.fagu.fmv.ffmpeg.filter.Filter.class.isAssignableFrom(c)) {
					int mod = c.getModifiers();
					return ! Modifier.isAbstract(mod) && ! Modifier.isInterface(mod);
				}
				return false;
			})) {
				try {
					@SuppressWarnings("unchecked")
					Class<org.fagu.fmv.ffmpeg.filter.Filter> cmdClass = (Class<org.fagu.fmv.ffmpeg.filter.Filter>)findCls;
					Constructor<org.fagu.fmv.ffmpeg.filter.Filter> constructor = cmdClass.getDeclaredConstructor(new Class[0]);
					constructor.setAccessible(true);
					org.fagu.fmv.ffmpeg.filter.Filter filter = constructor.newInstance();
					filters.put(filter.name(), filter);
				} catch(Exception ignore) {
					// Do nothing
				}
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return filters;
	}
}
