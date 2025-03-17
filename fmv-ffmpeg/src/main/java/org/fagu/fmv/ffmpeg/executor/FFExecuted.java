package org.fagu.fmv.ffmpeg.executor;

import java.util.OptionalLong;

import org.fagu.fmv.soft.SoftExecutor.Executed;

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
public interface FFExecuted<R> {

	OptionalLong getPID();

	long getExecuteTime();

	R getResult();

	int getExitValue();

	// **************************************

	static <R> FFExecuted<R> create(Executed executed, R result) {
		return create(executed.getPID(), executed.getExitValue(), executed.getExecuteTime(), result);
	}

	static <R> FFExecuted<R> create(OptionalLong pid, int exitValue, long executeTime, R result) {
		return new FFExecutedImpl<R>(pid, exitValue, executeTime, result);
	}

	// -------------------------------------------

	static class FFExecutedImpl<R> extends Executed implements FFExecuted<R> {

		private final R result;

		private FFExecutedImpl(OptionalLong pid, int exitValue, long executeTime, R result) {
			super(pid, exitValue, executeTime);
			this.result = result;
		}

		@Override
		public R getResult() {
			return result;
		}

	}

}
