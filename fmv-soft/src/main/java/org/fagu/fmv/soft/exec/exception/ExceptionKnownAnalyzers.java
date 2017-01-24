package org.fagu.fmv.soft.exec.exception;

/*-
 * #%L
 * fmv-soft
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

import org.fagu.fmv.utils.order.OrderComparator;


/**
 * @author fagu
 */
public class ExceptionKnownAnalyzers {

	/**
	 * 
	 */
	private ExceptionKnownAnalyzers() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * @param cls
	 * @return
	 */
	public static <A extends ExceptionKnownAnalyzer> List<A> getExceptionKnownAnalyzers(Class<A> cls) {
		List<A> list = new ArrayList<>();
		for(A exceptionKnown : ServiceLoader.load(cls)) {
			list.add(exceptionKnown);
		}
		OrderComparator.sort(list);
		return list;
	}

	/**
	 * @param cls
	 * @param e
	 * @return
	 */
	public static Optional<ExceptionKnown> getKnown(Class<? extends ExceptionKnownAnalyzer> cls, Exception e) {
		NestedException nestedException = new NestedException(e);
		return getExceptionKnownAnalyzers(cls).stream() //
				.map(ek -> ek.anaylze(nestedException)) //
				.filter(Objects::nonNull) //
				.findFirst();
	}

	/**
	 * @param cls
	 * @param e
	 * @param exceptionKnowConsumer
	 * @throws E
	 */
	@SuppressWarnings("unchecked")
	public static <E extends IOException> void doOrThrows(Class<? extends ExceptionKnownAnalyzer> cls, E exception,
			ExceptionKnowConsumer exceptionKnowConsumer) throws E {

		boolean isKnown = false;
		if(exceptionKnowConsumer != null) {
			Optional<ExceptionKnown> known = ExceptionKnownAnalyzers.getKnown(cls, exception);
			if(known.isPresent()) {
				isKnown = true;
				try {
					exceptionKnowConsumer.accept(known.get());
				} catch(IOException e1) {
					throw (E)e1;
				}
			}
		}
		if( ! isKnown) {
			throw exception;
		}
	}

}