package org.fagu.fmv.mymedia.movie.age.validator;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author f.agu
 * @created 8 juil. 2018 14:49:44
 */
public class WritableFuture<V> implements Future<V> {

	private final CountDownLatch latch = new CountDownLatch(1);

	private V value;

	private Exception exception;

	void setValue(V v) {
		value = v;
		latch.countDown();
	}

	void setException(Exception exception) {
		this.exception = exception;
		latch.countDown();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// not supported
		return false;
	}

	@Override
	public boolean isCancelled() {
		// not supported
		return false;
	}

	@Override
	public boolean isDone() {
		return latch.getCount() == 0;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		latch.await();
		if(exception != null) {
			throw new ExecutionException(exception);
		}
		return value;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if( ! latch.await(timeout, unit)) {
			throw new TimeoutException();
		}
		if(exception != null) {
			throw new ExecutionException(exception);
		}
		return value;
	}

}
