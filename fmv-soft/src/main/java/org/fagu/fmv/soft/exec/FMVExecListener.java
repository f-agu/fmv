package org.fagu.fmv.soft.exec;

import java.io.IOException;

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

import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteResultHandler;


/**
 * @author f.agu
 */
public interface FMVExecListener {

	/**
	 * @param fmvExecutor
	 * @param command
	 * @param environment
	 */
	default void eventPreExecute(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
			ExecuteResultHandler handler) {}

	/**
	 * @param fmvExecutor
	 * @param command
	 * @param environment
	 * @param handler
	 */
	default void eventPostExecute(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
			ExecuteResultHandler handler) {}

	/**
	 * @param fmvExecutor
	 * @param command
	 * @param environment
	 * @param handler
	 * @param ioe
	 */
	default void eventFailed(FMVExecutor fmvExecutor, CommandLine command, @SuppressWarnings("rawtypes") Map environment,
			ExecuteResultHandler handler, IOException ioe) {}

}
