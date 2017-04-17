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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.fagu.fmv.utils.ClassResolver;


/**
 * @author f.agu
 */
public class Factory<T> {

	private final Map<String, Class<T>> clsMap = new HashMap<>();

	private final Class<T> clazz;

	private final Function<T, String> keyFunction;

	/**
	 * @param clazz
	 * @param keyFunction
	 */
	public Factory(Class<T> clazz, Function<T, String> keyFunction) {
		this.clazz = Objects.requireNonNull(clazz);
		this.keyFunction = Objects.requireNonNull(keyFunction);
	}

	/**
	 * @param packageName
	 * @throws IOException
	 */
	public void register(String packageName) throws IOException {
		ClassResolver classResolver = new ClassResolver();

		// filters
		for(Class<?> cls : classResolver.find(packageName, cls -> {
			if(clazz.isAssignableFrom(cls)) {
				int mod = cls.getModifiers();
				return ! Modifier.isAbstract(mod) && ! Modifier.isInterface(mod) && Modifier.isPublic(mod);
			}
			return false;
		})) {
			@SuppressWarnings("unchecked")
			Class<T> tClass = (Class<T>)cls;
			T t = instanciate(tClass);
			if(t != null) {
				String apply = keyFunction.apply(t);
				if(apply != null) {
					clsMap.put(apply, tClass);
				}
			}
		}
	}

	/**
	 * @param code
	 * @return
	 */
	public Class<T> getClass(String code) {
		Class<T> tClass = clsMap.get(code);
		if(tClass == null) {
			notFound(code);
		}
		return tClass;
	}

	/**
	 * @param code
	 * @return
	 */
	public T get(String code) {
		Class<T> tClass = clsMap.get(code);
		if(tClass == null) {
			return notFound(code);
		}
		return instanciate(tClass);
	}

	// ********************************************

	/**
	 * @param code
	 * @return
	 */
	protected T notFound(String code) {
		throw new IllegalArgumentException("Code undefined: " + code);
	}

	/**
	 * @param cls
	 * @return
	 */
	protected T instanciate(Class<T> cls) {
		try {
			cls.getConstructor(new Class[0]);
			return cls.newInstance();
		} catch(NoSuchMethodException e) {
			return null;
		} catch(Exception e) {
			throw new RuntimeException("Error to instanciate " + cls, e);
		}
	}

}
