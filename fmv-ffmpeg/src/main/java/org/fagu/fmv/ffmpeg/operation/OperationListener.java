package org.fagu.fmv.ffmpeg.operation;

/*
 * #%L
 * fmv-ffmpeg
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

import org.fagu.fmv.ffmpeg.executor.FFExecutor;
import org.fagu.fmv.ffmpeg.filter.Filter;
import org.fagu.fmv.soft.exec.FMVExecListener;


/**
 * @author f.agu
 */
public interface OperationListener extends FMVExecListener {

	/**
	 * @param operation
	 * @param ioEntity
	 */
	default void eventCreate(FFExecutor<?> ffExecutor) {}

	// ===== ADD IOEntity =====

	/**
	 * @param operation
	 * @param ioEntity
	 */
	default void eventPreAddIOEntity(Operation<?, ?> operation, IOEntity ioEntity) {}

	/**
	 * @param operation
	 * @param ioEntity
	 */
	default void eventPostAddIOEntity(Operation<?, ?> operation, IOEntity ioEntity) {}

	// ===== ADD Parameter =====

	/**
	 * @param operation
	 * @param parameter
	 */
	default void eventPreAddParameter(Operation<?, ?> operation, Parameter parameter) {}

	/**
	 * @param operation
	 * @param parameter
	 */
	default void eventPostAddParameter(Operation<?, ?> operation, Parameter parameter) {}

	// ===== ADD Filter =====

	/**
	 * @param operation
	 * @param filter
	 */
	default void eventPreAddFilter(Operation<?, ?> operation, Filter filter) {}

	/**
	 * @param operation
	 * @param filter
	 */
	default void eventPostAddFilter(Operation<?, ?> operation, Filter filter) {}

	// ===== to arguments =====

	/**
	 * @param operation
	 */
	default void eventPreToArguments(Operation<?, ?> operation) {}

	/**
	 * @param operation
	 */
	default void eventPostToArguments(Operation<?, ?> operation) {}

}
