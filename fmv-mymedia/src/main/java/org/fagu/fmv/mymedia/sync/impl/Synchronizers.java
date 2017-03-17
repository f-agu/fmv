package org.fagu.fmv.mymedia.sync.impl;

/*
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2016 fagu
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

import java.io.PrintStream;

import org.fagu.fmv.mymedia.sync.Synchronizer;


/**
 * @author f.agu
 */
public class Synchronizers {

	/**
	 * 
	 */
	private Synchronizers() {}

	// --------------------------------------------------

	/**
	 * @author f.agu
	 *
	 * @param <T>
	 */
	// public interface ResultAggregator<T> {
	//
	// /**
	// * @param t
	// */
	// void aggregate(T t);
	// }

	// --------------------------------------------------

	/**
	 * @param logPrintStream
	 * @return
	 */
	public static Synchronizer getDefault(PrintStream logPrintStream) {
		return new ConfirmDeleteSynchronizer(new LogSynchronizer(new StandardSynchronizer(), logPrintStream));
	}

	/**
	 * @param synchronizers
	 * @return
	 */
	// public static Synchronizer sequenceOf(Collection<Synchronizer> synchronizers) {
	// return (Synchronizer)Proxy.newProxyInstance(Synchronizers.class.getClassLoader(), new Class[]
	// {Synchronizer.class}, new InvocationHandler() {
	//
	// /**
	// * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method,
	// * java.lang.Object[])
	// */
	// @Override
	// public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	// if("toString".equals(method) && args.length == 0) {
	// return synchronizers.stream().map(Synchronizer::toString).collect(Collectors.joining(", "));
	// }
	// for(Synchronizer synchronizer : synchronizers) {
	// Object result = method.invoke(synchronizer, args);
	// }
	// return null;
	// }
	// });
	// }

}
