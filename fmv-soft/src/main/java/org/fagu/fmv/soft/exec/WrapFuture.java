package org.fagu.fmv.soft.exec;

import java.util.Objects;

/*
 * #%L
 * fmv-utils
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;


/**
 * @author f.agu
 */
public class WrapFuture<I, O> implements Future<O> {

	private final Future<I> delegated;

	private final Function<I, O> converter;

	public WrapFuture(Future<I> delegated, Function<I, O> converter) {
		this.delegated = Objects.requireNonNull(delegated);
		this.converter = Objects.requireNonNull(converter);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return delegated.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return delegated.isCancelled();
	}

	@Override
	public boolean isDone() {
		return delegated.isDone();
	}

	@Override
	public O get() throws InterruptedException, ExecutionException {
		return converter.apply(delegated.get());
	}

	@Override
	public O get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return converter.apply(delegated.get(timeout, unit));
	}

	public Future<I> delegated() {
		return delegated;
	}

}
