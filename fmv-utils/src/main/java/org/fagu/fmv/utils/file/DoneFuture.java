package org.fagu.fmv.utils.file;

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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author f.agu
 */
public class DoneFuture<V> implements Future<V> {

	private final V value;

	/**
	 * @param value
	 */
	public DoneFuture(V value) {
		this.value = value;
	}

	/**
	 * @param mayInterruptIfRunning
	 * @return
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	/**
	 * @return
	 */
	@Override
	public boolean isCancelled() {
		return false;
	}

	/**
	 * @return
	 */
	@Override
	public boolean isDone() {
		return true;
	}

	/**
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Override
	public V get() throws InterruptedException, ExecutionException {
		return value;
	}

	/**
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return value;
	}

}
