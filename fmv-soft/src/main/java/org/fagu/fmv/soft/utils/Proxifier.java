package org.fagu.fmv.soft.utils;

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

	public Proxifier(Class<T> clazz) {
		this.clazz = Objects.requireNonNull(clazz);
		if( ! Modifier.isInterface(clazz.getModifiers())) {
			throw new IllegalArgumentException("Must be an interface: " + clazz.getName());
		}
		toCallList = new ArrayList<>();
	}

	public Proxifier<T> add(T toCall) {
		toCallList.add(toCall);
		return this;
	}

	public Proxifier<T> addAll(Collection<? extends T> toCalls) {
		toCallList.addAll(toCalls);
		return this;
	}

	public T proxify() {
		return proxifyVolatile(clazz, toCallList);
	}

	@SuppressWarnings("unchecked")
	public static <C> C proxifyVolatile(Class<C> clazz, List<C> list) {
		return (C)Proxy.newProxyInstance(Proxifier.class.getClassLoader(), new Class[] {clazz}, (proxy, method, args) -> {
			for(C toCall : list) {
				method.invoke(toCall, args);
			}
			return null;
		});
	}

}
