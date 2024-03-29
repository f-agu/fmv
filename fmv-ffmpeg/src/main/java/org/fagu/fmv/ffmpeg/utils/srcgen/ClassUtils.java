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

/**
 * @author f.agu
 */
public class ClassUtils {

	private ClassUtils() {}

	public static String typeOf(Param param, boolean primitive) {
		ParamType<?> paramType = param.getType();
		if(paramType == ParamType.FLAGS || ! param.getValues().isEmpty()) {
			return ClassNameUtils.type(param.getName());
		}
		Class<?> cls = paramType.getCls();
		if( ! primitive) {
			cls = org.apache.commons.lang3.ClassUtils.primitiveToWrapper(cls);
		} else {
			cls = org.apache.commons.lang3.ClassUtils.wrapperToPrimitive(cls);
		}
		if(cls == null) {
			cls = paramType.getCls();
		}
		if(cls == null) {
			throw new NullPointerException("Class for param " + param.getName());
		}
		return cls.getSimpleName();
	}

	public static String nameOf(Param param) {
		return ClassNameUtils.field(param.getName());
	}
}
