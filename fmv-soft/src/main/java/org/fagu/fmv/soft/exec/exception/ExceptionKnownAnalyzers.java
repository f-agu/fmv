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
	public static List<ExceptionKnownAnalyzer> getExceptionKnownAnalyzers(Class<? extends ExceptionKnownAnalyzer> cls) {
		List<ExceptionKnownAnalyzer> list = new ArrayList<>();
		for(ExceptionKnownAnalyzer exceptionKnown : ServiceLoader.load(cls)) {
			list.add(exceptionKnown);
		}
		for(ExceptionKnownAnalyzer exceptionKnown : ServiceLoader.load(ExceptionKnownAnalyzer.class)) {
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
	public static Optional<ExceptionKnown> getKnown(Class<? extends ExceptionKnownAnalyzer> cls, IOException e) {
		NestedException nestedException = new NestedException(e);
		return getExceptionKnownAnalyzers(cls).stream()
				.peek(eka -> System.out.println("ALL: " + eka))
				.map(ek -> ek.anaylze(nestedException))
				.filter(Objects::nonNull)
				.peek(eka -> System.out.println("FILTERED: " + eka))
				.findFirst();
	}

	/**
	 * @param cls
	 * @param exception
	 * @param exceptionKnownConsumer
	 * @param exceptionConsumer
	 * @throws E
	 */
	@SuppressWarnings("unchecked")
	public static <E extends IOException> void doOrThrows(Class<? extends ExceptionKnownAnalyzer> cls, E exception,
			ExceptionKnownConsumer exceptionKnownConsumer, ExceptionConsumer exceptionConsumer) throws E {

		boolean applied = false;
		if(exceptionKnownConsumer != null) {
			Optional<ExceptionKnown> known = ExceptionKnownAnalyzers.getKnown(cls, exception);
			if(known.isPresent()) {
				try {
					exceptionKnownConsumer.accept(known.get());
					applied = true;
				} catch(IOException e1) {
					throw (E)e1;
				}
			}
		} else if(exceptionConsumer != null) {
			try {
				exceptionConsumer.accept(exception);
				applied = true;
			} catch(IOException e1) {
				throw (E)e1;
			}
		}

		if( ! applied) {
			throw exception;
		}
	}

}
