package org.fagu.fmv.utils;

/*
 * #%L
 * fmv-utils
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

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * @author f.agu
 */
public class Proxifier<T> {

	private final Class<T> clazz;

	private final List<T> toCallList;

	/**
	 * @param clazz
	 */
	public Proxifier(Class<T> clazz) {
		this.clazz = Objects.requireNonNull(clazz);
		if( ! Modifier.isInterface(clazz.getModifiers())) {
			throw new IllegalArgumentException("Must be an interface: " + clazz.getName());
		}
		toCallList = new ArrayList<>();
	}

	/**
	 * @param toCall
	 * @return
	 */
	public Proxifier<T> add(T toCall) {
		toCallList.add(toCall);
		return this;
	}

	/**
	 * @param toCalls
	 * @return
	 */
	public Proxifier<T> addAll(Collection<? extends T> toCalls) {
		toCallList.addAll(toCalls);
		return this;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T proxify() {
		return (T)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {clazz}, (proxy, method, args) -> {
			for(T toCall : toCallList) {
				method.invoke(toCall, args);
			}
			return null;
		});
	}

}
